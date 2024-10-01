package ar.edu.utn.dds.k3003.model.controller;

import io.javalin.http.Context;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

public class MetricsController {

    private final String token;

    public MetricsController(){
        this.token = System.getenv("TOKEN");
    }

    public void crear(Context context, PrometheusMeterRegistry registry){

        // chequear el header de authorization y chequear el token bearer
        // configurado
        var auth = context.header("Authorization");

        if (auth != null && auth.intern() == "Bearer " + token) {
            context.contentType("text/plain; version=0.0.4")
                    .result(registry.scrape());
        } else {
            // si el token no es el apropiado, devolver error,
            // desautorizado
            // este paso es necesario para que Grafana online
            // permita el acceso
            context.status(401).json("unauthorized access " + this.token);
        }
    }

}
