package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record MaratonRequest(
        @NotBlank String nombre,
        String descripcion,
        @NotNull LocalDateTime fechaInicio,
        @NotNull LocalDateTime fechaFin
) {}
