package ar.edu.utn.dds.k3003.model.mappers;

import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.model.Temperatura;


public class TemperaturaMapper {
    public TemperaturaDTO map(Temperatura temperatura){
        TemperaturaDTO temperaturaDTO = new TemperaturaDTO(temperatura.getTemperatura(),temperatura.getHeladera().getId().intValue(),temperatura.getFechaMedicion());
        return temperaturaDTO;
    }
}
