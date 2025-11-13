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

    // --- Campos para problemas importados de plataformas externas (Codeforces, etc.) ---
    @Column(name = "source_platform", length = 50)
    private String sourcePlatform; // ej: "codeforces"

    @Column(name = "external_id", length = 50)
    private String externalId; // ej: "4A" (contestId+index) o solo index

    @Column(name = "source_url", length = 255)
    private String sourceUrl; // URL al problema oficial

    @Column(name = "difficulty")
    private Integer difficulty; // rating Codeforces (ej: 800, 1000)

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // JSON string: ["implementation","math"]

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary; // Resumen corto propio, no el enunciado completo para evitar copyright

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "problema", fetch = FetchType.LAZY)
    private Set<Envio> envios;

    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
    }
}
