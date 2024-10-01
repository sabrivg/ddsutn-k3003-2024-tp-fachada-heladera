package ar.edu.utn.dds.k3003.clientes;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface ViandasRetrofitClient {
    @GET("viandas/{qr}") //cuando haga get viandas/qr
    Call<ViandaDTO> get(@Path("qr") String qr); //devuelve un ViandaDTO
    @PATCH("viandas/{qr}/estado")
    Call<ViandaDTO> modifEstadoVianda(@Path("qr") String qr, @Body String estadoViandaEnum);
    @PATCH("viandas/{qr}")
    Call<ViandaDTO> modifHeladeraVianda(@Path("qr") String qr, @Body HeladeraDTO heladeraDto);

}
