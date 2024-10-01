package ar.edu.utn.dds.k3003.clientes.workers;

import ar.edu.utn.dds.k3003.clientes.workers.strategy.MensajeStrategy;
import ar.edu.utn.dds.k3003.clientes.workers.strategy.MensajeStrategyFactory;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;

public class MensajeListener extends DefaultConsumer {

    private final MensajeStrategyFactory mensajeStrategyFactory;

    private MensajeListener(Channel channel) {
        super(channel);
        this.mensajeStrategyFactory = new MensajeStrategyFactory();
    }

    // Inicializa la instancia del SensorTemperatura y la conexión
    public static void iniciar() throws Exception {
        // Establecer la conexión con CloudAMQP usando las variables de entorno
        Map<String, String> env = System.getenv();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.get("QUEUE_HOST"));
        factory.setUsername(env.get("QUEUE_USERNAME"));
        factory.setPassword(env.get("QUEUE_PASSWORD"));
        factory.setVirtualHost(env.get("QUEUE_USERNAME")); // El VHOST suele ser el mismo que el usuario

        String colaSensorTemperaturas = env.get("QUEUE_SENSOR_TEMPERATURA");
        String colaPrueba = env.get("QUEUE_PRUEBA");

        // Conexión y canal
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Crear una nueva instancia de SensorTemperatura
        MensajeListener sensorTemperatura = new MensajeListener(channel);
        sensorTemperatura.iniciarConsumo(colaSensorTemperaturas); // Iniciar el consumo de mensajes

        MensajeListener prueba = new MensajeListener(channel);
        prueba.iniciarConsumo(colaPrueba); // Iniciar el consumo de mensajes
    }

    // Iniciar el consumo de mensajes
    private void iniciarConsumo(String colaName) throws IOException {
        // Declarar la cola si no existe
        this.getChannel().queueDeclare(colaName, false, false, false, null);
        // Iniciar el consumo de mensajes
        this.getChannel().basicConsume(colaName, false, this);
        System.out.println("Esperando mensajes en la cola " + colaName + "...");
    }

    // Manejo de la entrega de mensajes
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        // Confirmar recepción
        this.getChannel().basicAck(envelope.getDeliveryTag(), false);

        System.out.println();
        System.out.println("Mensaje recibido desde la cola " + envelope.getRoutingKey());

        try {
            // Sacar el tipo de mensaje
            String tipoMensaje = properties.getType();

            if(tipoMensaje == null || tipoMensaje.isBlank()) {
                throw new Exception("No se pudo procesar el mensaje debido a que no se coloco el tipo de mensaje en las properies...");
            }

            MensajeStrategy mensajeStrategy = mensajeStrategyFactory.getStrategy(tipoMensaje);
            mensajeStrategy.procesarMensage(body);

        } catch (Exception e){
            System.err.println("ERROR: No se puede procesar el mensaje debido a que no es un tipo de mensaje permitido...");
        }
    }

}
