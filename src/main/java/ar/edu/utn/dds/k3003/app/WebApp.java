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

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class WebApp {

    public static void main(String[] args) {

        // Iniciar el worker en un hilo separado
        iniciarWorkerSensorTemperaturas();

        // Iniciar la API
        Javalin app = iniciarApiJavalin();

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
    private static Javalin iniciarApiJavalin() {
        int port = Integer.parseInt(System.getProperty("port", "8080")); // Usar puerto 8080 por defecto
        return Javalin.create().start(port);
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
