package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clientes.ViandasProxy;
import ar.edu.utn.dds.k3003.model.controller.HeladeraController;
import ar.edu.utn.dds.k3003.model.controller.TemperaturaController;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import ar.edu.utn.dds.k3003.clientes.workers.MensajeListener;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.micrometer.MicrometerPlugin;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class WebApp {
    private static final String TOKEN = "token";
    public static void main(String[] args) {
        // crea un registro para nuestras métricas basadas en Prometheus
        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        // Iniciar el worker en un hilo separado
        iniciarWorkerSensorTemperaturas();

        // Iniciar la API
        Javalin app = iniciarApiJavalin(registry);

        var objectMapper = createObjectMapper();
        var fachada = crearFachada(objectMapper);

        var heladeraController = new HeladeraController(fachada);
        var temperaturaController = new TemperaturaController(fachada);

        // Definir las rutas de la API
        app.post("/heladeras", heladeraController::agregar);
        app.get("/heladeras/{id}", heladeraController::obtener);
        //app.post("/temperaturas", temperaturaController::agregar);
        app.get("/heladeras/{id}/temperaturas", temperaturaController::obtener);
        app.post("/depositos", heladeraController::depositar);
        app.post("/retiros", heladeraController::retirar);
        app.get("/cleanup", heladeraController::cleanup);

        // agregamos una ruta para hacer polling de las métrivas con el
        // handler que toma llos resultadose scrapear la registry de
        // métricas
        app.get("/metrics",
                ctx -> {
                    // chequear el header de authorization y chequear el token bearer
                    // configurado
                    var auth = ctx.header("Authorization");

                    if (auth != null && auth.intern() == "Bearer " + TOKEN) {
                        ctx.contentType("text/plain; version=0.0.4")
                                .result(registry.scrape());
                    } else {
                        // si el token no es el apropiado, devolver error,
                        // desautorizado
                        // este paso es necesario para que Grafana online
                        // permita el acceso
                        ctx.status(401).json("unauthorized access");
                    }
                });
    }

    // Iniciar el worker de RabbitMQ
    private static void iniciarWorkerSensorTemperaturas() {
        Thread workerThread = new Thread(() -> {
            try {
                MensajeListener.iniciar(); // Inicia el worker de RabbitMQ
            } catch (Exception e) {
                System.err.println("Error al iniciar el worker de RabbitMQ: " + e.getMessage());
                e.printStackTrace();
            }
        });
        workerThread.start(); // Iniciar el hilo del worker
    }

    // Crear la fachada
    private static Fachada crearFachada(ObjectMapper objectMapper) {
        var fachada = new Fachada();
        fachada.setViandasProxy(new ViandasProxy(objectMapper)); // Usar ViandasProxy para pruebas locales
        return fachada;
    }

    // Inicializar la API
    private static Javalin iniciarApiJavalin(PrometheusMeterRegistry registry) {

        System.out.println("starting up the server");


        // agregar aquí cualquier tag que aplique a todas las métrivas de la app
        // (e.g. EC2 region, stack, instance id, server group)
        registry.config().commonTags("app", "metrics-sample");

        // agregamos a nuestro reigstro de métricas todo lo relacionado a infra/tech
        // de la instancia y JVM
        try (var jvmGcMetrics = new JvmGcMetrics();
             var jvmHeapPressureMetrics = new JvmHeapPressureMetrics()) {
            jvmGcMetrics.bindTo(registry);
            jvmHeapPressureMetrics.bindTo(registry);
        }
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new FileDescriptorMetrics().bindTo(registry);

        // agregamos métricas custom de nuestro dominio
        Gauge.builder("myapp_random", () -> (int)(Math.random() * 1000))
                .description("Random number from My-Application.")
                .strongReference(true)
                .register(registry);

        // seteamos el registro dentro de la config de Micrometer
        final var micrometerPlugin =
                new MicrometerPlugin(config -> config.registry = registry);

        // registramos el plugin de Micrometer dentro de la config de la app de
        // Javalin
         return Javalin.create(config -> { config.registerPlugin(micrometerPlugin); })
                .start(8080);


    }

    // Configurar el ObjectMapper
    public static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        configureObjectMapper(objectMapper);
        return objectMapper;
    }

    public static void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
    }
}
