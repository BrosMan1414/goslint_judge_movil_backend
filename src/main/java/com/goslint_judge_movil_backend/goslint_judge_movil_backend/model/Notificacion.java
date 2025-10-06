package com.goslint_judge_movil_backend.goslint_judge_movil_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="notificaciones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="equipo_id", nullable=false)
    private Equipo equipo;

    @Column(nullable=false, length=255)
    private String mensaje;

    private Boolean leido;

    private LocalDateTime fecha;

    @PrePersist
    void prePersist() {
        if (fecha == null) fecha = LocalDateTime.now();
        if (leido == null) leido = Boolean.FALSE;
    }
}
