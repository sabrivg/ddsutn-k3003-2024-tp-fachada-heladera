package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.model.Temperatura;
import ar.edu.utn.dds.k3003.model.mappers.HeladeraMapper;
import ar.edu.utn.dds.k3003.repositories.HeladerasRepository;
import ar.edu.utn.dds.k3003.model.mappers.TemperaturaMapper;
import ar.edu.utn.dds.k3003.repositories.TemperaturaRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Fachada implements FachadaHeladeras {

 private final HeladerasRepository heladerasRepository;
 private final HeladeraMapper heladeraMapper;
 private final TemperaturaRepository temperaturaRepository;
 private final TemperaturaMapper temperaturaMapper;
 private FachadaViandas fachadaViandas;

 public Fachada() {
  this.heladerasRepository = new HeladerasRepository();
  this.heladeraMapper = new HeladeraMapper();
  this.temperaturaRepository=new TemperaturaRepository();
  this.temperaturaMapper=new TemperaturaMapper();
 }
    public Fachada(HeladerasRepository heladerasRepository, HeladeraMapper heladeraMapper, TemperaturaRepository temperaturaRepository, TemperaturaMapper temperaturaMapper) {
        this.heladerasRepository = heladerasRepository;
        this.heladeraMapper = heladeraMapper;
        this.temperaturaRepository = temperaturaRepository;
        this.temperaturaMapper = temperaturaMapper;
    }

    @Override public HeladeraDTO agregar(HeladeraDTO heladeraDTO){
    Heladera heladera= new Heladera(heladeraDTO.getNombre());
    heladera = this.heladerasRepository.save(heladera);
    return heladeraMapper.map(heladera);
    }

    @Override public void depositar(Integer heladeraId, String qrVianda) throws NoSuchElementException {
        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(heladeraId))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + heladeraId));
       ViandaDTO vianda = this.fachadaViandas.buscarXQR(qrVianda);

        if(vianda.getEstado() != EstadoViandaEnum.PREPARADA
                && vianda.getEstado()!=EstadoViandaEnum.EN_TRASLADO){
            throw new RuntimeException("La Vianda "+qrVianda+ " no esta preparada ni en traslado, no se puede depositar");
        }
        if(vianda.getEstado() == EstadoViandaEnum.PREPARADA){//evitar doble llamado con traslados
            fachadaViandas.modificarEstado(qrVianda, EstadoViandaEnum.DEPOSITADA);
        }
        heladera.depositarVianda();
        this.heladerasRepository.update(heladera);
    }

    @Override public Integer cantidadViandas(Integer heladeraId) throws NoSuchElementException{
        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(heladeraId))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + heladeraId));
      return heladera.getViandas();
 }

    @Override public void retirar(RetiroDTO retiro) throws NoSuchElementException{
        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(retiro.getHeladeraId()))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + retiro.getHeladeraId()));
        ViandaDTO vianda = this.fachadaViandas.buscarXQR(retiro.getQrVianda());

        if(vianda.getEstado()!=EstadoViandaEnum.DEPOSITADA){
            throw new RuntimeException("La Vianda "+retiro.getQrVianda()+ " no esta depositada, no se puede retirar");
        }
        if(!Objects.equals(vianda.getHeladeraId(), retiro.getHeladeraId())){//la heladera de la que se quiere retirar la vianda es la heladera en la que realmente esta la vianda
            throw new RuntimeException("La Vianda "+retiro.getQrVianda()+ " no esta depositada en esta heladera "+retiro.getHeladeraId());
        }
        fachadaViandas.modificarEstado(retiro.getQrVianda(), EstadoViandaEnum.RETIRADA);
        fachadaViandas.modificarHeladera(retiro.getQrVianda(),-1);
        try {
            heladera.retirarVianda();
            this.heladerasRepository.update(heladera);
        } catch (Exception e) {
            throw new RuntimeException("No hay viandas para retirar");
        }


    }

    @Override public void temperatura(TemperaturaDTO temperaturaDTO){
        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(temperaturaDTO.getHeladeraId()))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + temperaturaDTO.getHeladeraId()));

        Temperatura temperatura=new Temperatura(temperaturaDTO.getTemperatura(),heladera, LocalDateTime.now());
        temperatura=this.temperaturaRepository.save(temperatura);
    }

    @Override public List<TemperaturaDTO> obtenerTemperaturas(Integer heladeraId){
        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(heladeraId))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + heladeraId));

        List<Temperatura> temperaturas= temperaturaRepository.findAllById(heladera.getId());

        List<TemperaturaDTO> temperaturaDTOS = temperaturas.stream()
                .map(temperaturaMapper::map)
                .collect(Collectors.toList());

        Collections.reverse(temperaturaDTOS);
     return temperaturaDTOS;
    }

    @Override public void setViandasProxy(FachadaViandas viandas){
     this.fachadaViandas=viandas;
    }

    public HeladeraDTO obtenerHeladera(Integer id){
        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + id));

        return heladeraMapper.map(heladera);
    }

    public boolean clean() {
    temperaturaRepository.findAll().forEach(temperaturaRepository::delete);
     heladerasRepository.findAll().forEach(heladerasRepository::delete);
        return heladerasRepository.findAll().isEmpty() && temperaturaRepository.findAll().isEmpty();
    }
}
