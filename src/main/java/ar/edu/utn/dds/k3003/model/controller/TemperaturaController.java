package ar.edu.utn.dds.k3003.model.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import io.javalin.http.Context;

import java.util.NoSuchElementException;

public class TemperaturaController {
    Fachada fachada=new Fachada();

    public TemperaturaController(Fachada fachada) {
        this.fachada = fachada;
    }

    public void obtener(Context context){
        var id = context.pathParamAsClass("id", Integer.class).get();
        var status = 200;
        try {
            var temperaturaDTO = this.fachada.obtenerTemperaturas(id);
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Temperaturas obtenidas correctamente", temperaturaDTO);
            context.status(status).json(respuestaDTO);
        } catch (NoSuchElementException ex) {
            status = 404;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Heladera no encontrada", null);
            context.status(status).json(respuestaDTO);
        }
    }

    public void agregar(Context context) {
        var temperaturaDTO = context.bodyAsClass(TemperaturaDTO.class);
        var status = 200;
        try {
            this.fachada.temperatura(temperaturaDTO);
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Temperatura registrada correctamente", null);
            context.status(status).json(respuestaDTO);
        }
        catch (NoSuchElementException ne){
            ne.printStackTrace();
            status = 404;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Error de solicitud "+ne.getMessage(), null);
            context.status(status).json(respuestaDTO);
        }
        catch (Exception ex){
            ex.printStackTrace();
            status = 500;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Error interno "+ex.getMessage(), null);
            context.status(status).json(respuestaDTO);
        }
    }
}
