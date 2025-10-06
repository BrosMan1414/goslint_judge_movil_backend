package com.goslint_judge_movil_backend.goslint_judge_movil_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipos_maratones", uniqueConstraints = @UniqueConstraint(columnNames = {"equipo_id","maraton_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EquipoMaraton {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "maraton_id", nullable = false)
    private Maraton maraton;
}
