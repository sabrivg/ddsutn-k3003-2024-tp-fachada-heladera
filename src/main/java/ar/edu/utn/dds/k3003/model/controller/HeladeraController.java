package ar.edu.utn.dds.k3003.model.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import io.javalin.http.Context;

import java.util.NoSuchElementException;

public class HeladeraController {

    Fachada fachada;

    public HeladeraController(Fachada fachadaHeladera) {
        this.fachada =fachadaHeladera;
    }

    public void agregar(Context context) {
        var heladeraDTO = context.bodyAsClass(HeladeraDTO.class);
        var status = 200;
        try {
            var heladeraDTORta = this.fachada.agregar(heladeraDTO);
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Heladera agregada correctamente", heladeraDTORta);
            context.status(status).json(respuestaDTO);
        } catch(NoSuchElementException ex){
            status = 400;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Error de solicitud: " + ex.getMessage(), null);
            context.status(status).json(respuestaDTO);
        }
    }

    public void obtener(Context context) {
        var id = context.pathParamAsClass("id", Integer.class).get();
        var status = 200;
        try {
            var heladera = this.fachada.obtenerHeladera(id);
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Heladera obtenida correctamente", heladera);
            context.status(status).json(respuestaDTO);

        } catch (NoSuchElementException ex) {
            status = 404;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Heladera no encontrada", null);
            context.status(status).json(respuestaDTO);
        }
    }
    public void depositar(Context context) {
        var vianda = context.bodyAsClass(ViandaDTO.class);
        var status = 200;
        try {
            this.fachada.depositar(vianda.getHeladeraId(), vianda.getCodigoQR());
            RespuestaDTO respuestaDTO = new RespuestaDTO(200, "Vianda depositada correctamente", null);
            context.status(200).json(respuestaDTO);
        } catch (Exception ex){
            status = 400;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Error de solicitud: " + ex.getMessage(), null);
            context.status(status).json(respuestaDTO);
        }
    }
    public void retirar(Context context) {
        var retirar = context.bodyAsClass(RetiroDTO.class);
        var status = 200;
        try {
            this.fachada.retirar(retirar);
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Vianda retirada correctamente", null);
            context.status(status).json(respuestaDTO);
        }
        catch(Exception ex){
            status = 400;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Error de solicitud: "+ex.getMessage(), null);
            context.status(status).json(respuestaDTO);
        }
    }

    public void cleanup(Context context) {

        var status = 200;

        if(fachada.clean()){
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Se borro la BD con exito =)", null);
            context.status(status).json(respuestaDTO);
        }
        else{
            status = 400;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "No se borro la BD =(", null);
            context.status(status).json(respuestaDTO);
        }
    }
}
