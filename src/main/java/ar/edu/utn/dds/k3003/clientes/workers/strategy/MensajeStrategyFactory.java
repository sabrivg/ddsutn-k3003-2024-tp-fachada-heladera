package ar.edu.utn.dds.k3003.clientes.workers.strategy;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import java.util.HashMap;
import java.util.Map;

public class MensajeStrategyFactory {

    private final Map<String, MensajeStrategy> estrategias = new HashMap<String, MensajeStrategy>();

    public MensajeStrategyFactory(PrometheusMeterRegistry registry) {
        estrategias.put("temperatura", new SensorTemperaturaStrategy(registry));
        estrategias.put("error", new ErrorMensajeStrategy());
    }

    public MensajeStrategy getStrategy(String strategy) {

        MensajeStrategy estrategia = estrategias.get(strategy);
        if (estrategia == null) {
            estrategia = estrategias.get("error");
        }
        return estrategia;
    }

}
