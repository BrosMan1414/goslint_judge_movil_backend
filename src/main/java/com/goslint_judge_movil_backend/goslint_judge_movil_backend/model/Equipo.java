package com.goslint_judge_movil_backend.goslint_judge_movil_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "equipos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "email_contacto", nullable = false, unique = true, length = 150)
    private String emailContacto;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    private Integer puntaje;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "equipo", fetch = FetchType.LAZY)
    private Set<EquipoMaraton> inscripciones;

    @OneToMany(mappedBy = "equipo", fetch = FetchType.LAZY)
    private Set<Envio> envios;

    @OneToMany(mappedBy = "equipo", fetch = FetchType.LAZY)
    private Set<Notificacion> notificaciones;

    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if (puntaje == null) puntaje = 0;
    }
}
