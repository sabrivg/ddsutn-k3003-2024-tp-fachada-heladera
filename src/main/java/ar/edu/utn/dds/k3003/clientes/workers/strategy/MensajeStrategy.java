package ar.edu.utn.dds.k3003.clientes.workers.strategy;

import java.io.IOException;

public interface MensajeStrategy {
    void procesarMensage(byte[] body) throws IOException;
}
