package com.goslint_judge_movil_backend.goslint_judge_movil_backend.model;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.ResultadoEnvio;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "envios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="equipo_id", nullable=false)
    private Equipo equipo;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="problema_id", nullable=false)
    private Problema problema;

    @Column(nullable=false, length=50)
    private String lenguaje;

    @Column(nullable=false, columnDefinition = "TEXT")
    private String codigo;

    @Enumerated(EnumType.STRING)
    private ResultadoEnvio resultado;

    @Column(name="tiempo_ejecucion")
    private Double tiempoEjecucion;

    @Column(name="memoria_usada")
    private Integer memoriaUsada;

    private LocalDateTime fecha;

    @OneToMany(mappedBy = "envio", fetch = FetchType.LAZY)
    private Set<Retroalimentacion> retroalimentaciones;

    @PrePersist
    void prePersist() {
        if (fecha == null) fecha = LocalDateTime.now();
    }
}
