package ar.edu.utn.dds.k3003.clientes;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;

import retrofit2.converter.jackson.JacksonConverterFactory;
import java.util.List;
import java.util.NoSuchElementException;

public class ViandasProxy implements FachadaViandas {

    private final String endpoint;
    private final ViandasRetrofitClient service;

    public ViandasProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        this.endpoint = env.getOrDefault("URL_VIANDAS", "http://localhost:8081/");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(ViandasRetrofitClient.class);
    }

    @Override
    public ViandaDTO agregar(ViandaDTO viandaDTO) {
        return null;
    }

    @SneakyThrows
    @Override
    public ViandaDTO modificarEstado(String s, EstadoViandaEnum estadoViandaEnum) throws NoSuchElementException {

        Response<ViandaDTO> execute = service.modifEstadoVianda(s, estadoViandaEnum.name()).execute();

        if (execute.isSuccessful()) {
            return execute.body();
        }
        if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
            throw new NoSuchElementException("no se encontro la vianda " + s);
        }
        throw new RuntimeException("Error conectandose con el componente viandas");
    }
    @Override
    public List<ViandaDTO> viandasDeColaborador(Long aLong, Integer integer, Integer integer1)
            throws NoSuchElementException {
        return null;
    }

    @SneakyThrows
    @Override
    public ViandaDTO buscarXQR(String qr) throws NoSuchElementException {
        Response<ViandaDTO> execute = service.get(qr).execute(); //hace el request CON EXECUTE() y devuelve un ViandaDTO

        if (execute.isSuccessful()) {
            return execute.body(); //devuelve un ViandaDTO
        }
        if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
            throw new NoSuchElementException("no se encontro la vianda " + qr);
        }
        throw new RuntimeException("Error conectandose con el componente viandas");
    }

    @Override
    public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {}

    @Override
    public boolean evaluarVencimiento(String s) throws NoSuchElementException {
        return false;
    }

    @SneakyThrows
    @Override
    public ViandaDTO modificarHeladera(String s, int i) {

        HeladeraDTO heladeradto = new HeladeraDTO(i,null,null);
        Response<ViandaDTO> execute = service.modifHeladeraVianda(s, heladeradto).execute();

        if (execute.isSuccessful()) {
            return execute.body();
        }
        if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
            throw new NoSuchElementException("no se encontro la vianda " + s);
        }
        throw new RuntimeException("Error conectandose con el componente viandas");
    }
}