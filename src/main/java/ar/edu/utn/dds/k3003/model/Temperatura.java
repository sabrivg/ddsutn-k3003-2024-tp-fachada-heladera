package ar.edu.utn.dds.k3003.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="temperatura")
public class Temperatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Integer temperatura;
    @ManyToOne
    @JoinColumn(name = "heladera_id", nullable = false)
    private Heladera heladera;
    @Column
    private LocalDateTime fechaMedicion;

    public Temperatura(Integer temperatura, Heladera heladera, LocalDateTime fechaMedicion) {
        this.temperatura = temperatura;
        this.heladera = heladera;
        this.fechaMedicion = LocalDateTime.now();
    }


    public Temperatura() {
    }
}
