package com.goslint_judge_movil_backend.goslint_judge_movil_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "maratones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Maraton {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name="fecha_inicio", nullable=false)
    private LocalDateTime fechaInicio;

    @Column(name="fecha_fin", nullable=false)
    private LocalDateTime fechaFin;

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "maraton", fetch = FetchType.LAZY)
    private Set<EquipoMaraton> inscripciones;

    @OneToMany(mappedBy = "maraton", fetch = FetchType.LAZY)
    private Set<Problema> problemas;

    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
    }
}
