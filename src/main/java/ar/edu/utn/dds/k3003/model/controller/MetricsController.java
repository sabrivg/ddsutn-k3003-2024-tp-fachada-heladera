package ar.edu.utn.dds.k3003.model.controller;

import ar.edu.utn.dds.k3003.utils.MetricsRegistry;
import io.javalin.http.Context;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.jetbrains.annotations.NotNull;

public class MetricsController {
    public MetricsController() {}

    public void obtener(@NotNull Context context) {

        // chequear el header de authorization y chequear el token bearer
        // configurado
        var auth = context.header("Authorization");
        if (auth != null && auth.intern().equals("Bearer " + System.getenv("TOKEN"))) {
            context.contentType("text/plain; version=0.0.4; charset=utf-8")
                    .result(MetricsRegistry.getRegistry().scrape());
        } else {
            context.status(401).json("unauthorized access");
        }
    }
}
