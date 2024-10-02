package ar.edu.utn.dds.k3003.clientes.workers.strategy;

import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.model.Temperatura;
import ar.edu.utn.dds.k3003.repositories.HeladerasRepository;
import ar.edu.utn.dds.k3003.repositories.TemperaturaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class SensorTemperaturaStrategy implements MensajeStrategy {

    private final ObjectMapper objectMapper;
    private final TemperaturaRepository temperaturaRepository;
    private final HeladerasRepository heladerasRepository;

    // Un mapa que contendrá las métricas de temperatura de cada heladera
    private static final ConcurrentHashMap<Long, AtomicReference<Double>> temperaturasPorHeladera = new ConcurrentHashMap<>();

    public SensorTemperaturaStrategy() {
        this.temperaturaRepository = new TemperaturaRepository();
        this.heladerasRepository = new HeladerasRepository();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void procesarMensage(byte[] body) throws IOException {
        TemperaturaDTO temperaturaDTO = objectMapper.readValue(body, TemperaturaDTO.class);
        procesarTemperatura(temperaturaDTO);
    }

    // Procesar la temperatura recibida
    private void procesarTemperatura(TemperaturaDTO temperaturaDTO) {
        Long heladeraId = Long.valueOf(temperaturaDTO.getHeladeraId());

        // Obtener o registrar la heladera
        Heladera heladera = this.heladerasRepository.findById(heladeraId)
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + heladeraId));

        // Guardar la temperatura en la base de datos
        Temperatura temperatura = new Temperatura(temperaturaDTO.getTemperatura(), heladera, LocalDateTime.now());
        this.temperaturaRepository.save(temperatura);

        // Actualizar o registrar la métrica para esta heladera
        actualizarMetricaHeladera(heladeraId, temperaturaDTO.getTemperatura());

        System.out.println("Se recibió la siguiente temperatura");
        System.out.println("Heladera ID: " + temperaturaDTO.getHeladeraId());
        System.out.println("Temperatura: " + temperaturaDTO.getTemperatura());
    }

    // Registrar o actualizar la métrica para una heladera específica
    private void actualizarMetricaHeladera(Long heladeraId, double nuevaTemperatura) {

        // Si la heladera ya tiene una métrica registrada, actualizamos la temperatura

        temperaturasPorHeladera.computeIfAbsent(heladeraId, id -> {
            // Crear y registrar una nueva métrica si no existe para esta heladera
            AtomicReference<Double> temperaturaRef = new AtomicReference<>(nuevaTemperatura);

            // agregamos métricas custom de nuestro dominio
            Gauge.builder("heladera.temperatura.actual", temperaturaRef, AtomicReference::get)
                    .description("Temperatura actual de la heladera " + heladeraId)
                    .tag("heladeraId", String.valueOf(heladeraId))  // Añadir el ID como etiqueta
                    .strongReference(true)
                    .register(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));

            return temperaturaRef;
        }).set(nuevaTemperatura);  // Actualizamos la temperatura si ya existe
    }
}