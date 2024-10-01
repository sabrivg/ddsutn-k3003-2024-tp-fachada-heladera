package ar.edi.itn.dds.k3003.model.Heladeras;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.model.mappers.HeladeraMapper;
import ar.edu.utn.dds.k3003.repositories.HeladerasRepository;
import ar.edu.utn.dds.k3003.model.mappers.TemperaturaMapper;
import ar.edu.utn.dds.k3003.repositories.TemperaturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.description;

@ExtendWith(MockitoExtension.class)
public class HeladerasTest {

    Fachada instancia;
    HeladerasRepository repo;
    TemperaturaRepository repoT;

    @Mock
    FachadaViandas fachadaViandas;
    private void initializeHeladerasTest(){
        repo=new HeladerasRepository();
        repoT=new TemperaturaRepository();
        instancia=new Fachada(repo,new HeladeraMapper(),repoT,new TemperaturaMapper());
        instancia.setViandasProxy(fachadaViandas);
    }
    @BeforeEach
    public void initialize() {
        this.initializeHeladerasTest();
    }
 @Test
 void testAgregar() {
     instancia.agregar(new HeladeraDTO(12, "Una Heladera",0));
     instancia.agregar(new HeladeraDTO(34, "Una Heladera",0));

     //assertEquals(12,repo.findById(12L).getId(),"La heladera no se guardo correctamente");
 }

@Test
void testDepositar() {
    instancia.agregar(new HeladeraDTO(1, "Una Heladera",0));
    instancia.agregar(new HeladeraDTO(2, "Una Heladera",0));

    when(fachadaViandas.buscarXQR("22")).thenReturn(new ViandaDTO("22", LocalDateTime.now(), EstadoViandaEnum.PREPARADA, 20L, 2));
    when(fachadaViandas.buscarXQR("33")).thenReturn(new ViandaDTO("33", LocalDateTime.now(), EstadoViandaEnum.PREPARADA, 34L, 1));
    when(fachadaViandas.buscarXQR("44")).thenReturn(new ViandaDTO("44", LocalDateTime.now(), EstadoViandaEnum.PREPARADA, 20L, 2));

    instancia.depositar(2,"22");
    instancia.depositar(1,"33");
    instancia.depositar(2,"44");

    verify(fachadaViandas, description("no se marco la vianda como depositada")).modificarEstado("22", EstadoViandaEnum.DEPOSITADA);
    verify(fachadaViandas, description("no se marco la vianda como depositada")).modificarEstado("33", EstadoViandaEnum.DEPOSITADA);
    verify(fachadaViandas, description("no se marco la vianda como depositada")).modificarEstado("44", EstadoViandaEnum.DEPOSITADA);

    assertEquals(1, instancia.cantidadViandas(1), "Las viandas no se agregaron correctamente");
    assertEquals(2, instancia.cantidadViandas(2), "Las viandas no se agregaron correctamente");
    /*assertEquals("33",repo.findById(1L).getViandas().get(0));
    assertEquals("22",repo.findById(2L).getViandas().get(0));
    assertEquals("44",repo.findById(2L).getViandas().get(1));*/
 }

@Test
void testRetirarVianda() {
    instancia.agregar(new HeladeraDTO(1, "Una Heladera",0));
    instancia.agregar(new HeladeraDTO(2, "Una Heladera",0));

    when(fachadaViandas.buscarXQR("22")).thenReturn(new ViandaDTO("22", LocalDateTime.now(), EstadoViandaEnum.PREPARADA, 20L, 2));
    when(fachadaViandas.buscarXQR("33")).thenReturn(new ViandaDTO("33", LocalDateTime.now(), EstadoViandaEnum.PREPARADA, 34L, 1));
    when(fachadaViandas.buscarXQR("44")).thenReturn(new ViandaDTO("44", LocalDateTime.now(), EstadoViandaEnum.PREPARADA, 20L, 2));

    instancia.depositar(2,"22");
    instancia.depositar(1,"33");
    instancia.depositar(2,"44");

    instancia.retirar(new RetiroDTO("22", "14L", 2));
    instancia.retirar(new RetiroDTO("33", "14L", 1));

    verify(fachadaViandas, description("no se marco la vianda como retirada")).modificarEstado("22", EstadoViandaEnum.RETIRADA);
    verify(fachadaViandas, description("no se marco la vianda como retirada")).modificarEstado("33", EstadoViandaEnum.RETIRADA);

    //assertEquals("44",repo.findById(2L).getViandas().get(0));//la vianda que quedo en la heladera 2
    assertEquals(0, instancia.cantidadViandas(1));
    assertEquals(1, instancia.cantidadViandas(2));
}
@Test
void obtenerTemperatura(){
    TemperaturaDTO tempDto1 = new TemperaturaDTO(22,1, LocalDateTime.now());
    TemperaturaDTO tempDto2 = new TemperaturaDTO(24,1, LocalDateTime.now());

    instancia.agregar(new HeladeraDTO(1,"Juana",0));

    instancia.temperatura(tempDto1);
    instancia.temperatura(tempDto2);
  /*  assertEquals(tempDto1.getTemperatura(),repoT.findById(1L).get(0).getTemperatura());
    assertEquals(tempDto2.getTemperatura(),repoT.findById(1L).get(1).getTemperatura());
    assertEquals(2,repoT.findById(1L).size());*/
    }
}
