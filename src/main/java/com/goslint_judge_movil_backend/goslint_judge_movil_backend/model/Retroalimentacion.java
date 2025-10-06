package com.goslint_judge_movil_backend.goslint_judge_movil_backend.model;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.TipoRetro;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="retroalimentaciones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Retroalimentacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="envio_id", nullable=false)
    private Envio envio;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TipoRetro tipo;

    @Column(nullable=false, columnDefinition = "TEXT")
    private String comentario;

    private LocalDateTime fecha;

    @PrePersist
    void prePersist() {
        if (fecha == null) fecha = LocalDateTime.now();
    }
}
