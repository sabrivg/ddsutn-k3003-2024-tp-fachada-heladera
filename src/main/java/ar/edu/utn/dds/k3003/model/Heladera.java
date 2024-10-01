package ar.edu.utn.dds.k3003.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Data
@Entity
@Table(name="heladera")
public class Heladera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition ="varchar")
    private String nombre;
    @Column
    private int viandas;
    @Column
    int capacidadMax; //en unidad de viandas
    @Column
    LocalDate fechaInicioFuncionamiento;
    @Transient
    boolean activa;
   // Collection<Integer> temperaturas;
   @Transient
    float longitud;
    @Transient
    float latitud;
    @Transient
    String direccion;
    @Transient
    String ciudad;
    @Transient
    float temperaturaMin;
    @Transient
    float temperaturaMax;
    @Transient
    long cantidadAperturas;

    public Heladera(String nombre) {
        this.nombre=nombre;
        this.fechaInicioFuncionamiento=LocalDate.now();
        this.activa=true;
        //this.capacidadMax=20;//por ahora
        this.cantidadAperturas=0;
        this.viandas=0;
    }

    public Heladera() {
    }

    public void depositarVianda() {
        this.viandas++;
        this.cantidadAperturas++;
    }

    public void retirarVianda() {
        this.cantidadAperturas++;
        if (this.viandas > 0) {
            this.viandas--;
        } else {
            throw new NoSuchElementException("No hay viandas para remover");
        }
    }
}
