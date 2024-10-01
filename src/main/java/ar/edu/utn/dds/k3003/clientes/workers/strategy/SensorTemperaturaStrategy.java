package ar.edu.utn.dds.k3003.clientes.workers.strategy;

import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.model.Temperatura;
import ar.edu.utn.dds.k3003.repositories.HeladerasRepository;
import ar.edu.utn.dds.k3003.repositories.TemperaturaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

public class SensorTemperaturaStrategy implements MensajeStrategy {

    private final ObjectMapper objectMapper;
    private final TemperaturaRepository temperaturaRepository;
    private final HeladerasRepository heladerasRepository;

    public SensorTemperaturaStrategy() {
        this.temperaturaRepository = new TemperaturaRepository();
        this.heladerasRepository = new HeladerasRepository();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void procesarMensage(byte[] body) throws IOException {

        TemperaturaDTO temperatura = objectMapper.readValue(body, TemperaturaDTO.class);
        procesarTemperatura(temperatura);
    }

    // Procesar la temperatura recibida
    private void procesarTemperatura(TemperaturaDTO temperaturaDTO) {

        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(temperaturaDTO.getHeladeraId()))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + temperaturaDTO.getHeladeraId()));
        Temperatura temperatura = new Temperatura(temperaturaDTO.getTemperatura(), heladera, LocalDateTime.now());
        this.temperaturaRepository.save(temperatura);

        System.out.println("Se recibió la siguiente temperatura");
        System.out.println("Heladera ID: " + temperaturaDTO.getHeladeraId());
        System.out.println("Temperatura: " + temperaturaDTO.getTemperatura());
    }
}
