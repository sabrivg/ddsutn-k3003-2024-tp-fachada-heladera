package ar.edu.utn.dds.k3003.utils;

import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.Getter;

public class MetricsRegistry {
    @Getter
    private static final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    // Constructor privado para evitar instanciación
    private MetricsRegistry() {}
}
