package com.goslint_judge_movil_backend.goslint_judge_movil_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "problemas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Problema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "maraton_id", nullable = false)
    private Maraton maraton;

    @Column(nullable=false, length=150)
    private String titulo;

    @Column(nullable=false, columnDefinition = "TEXT")
    private String enunciado;

    @Column(name="limite_tiempo", nullable=false)
    private Integer limiteTiempo;

    @Column(name="limite_memoria", nullable=false)
    private Integer limiteMemoria;

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "problema", fetch = FetchType.LAZY)
    private Set<Envio> envios;

    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
    }
}
