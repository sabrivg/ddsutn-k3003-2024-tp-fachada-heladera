package ar.edu.utn.dds.k3003.clientes.workers.strategy;

public class ErrorMensajeStrategy implements MensajeStrategy {

    @Override
    public void procesarMensage(byte[] body) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }
}
