package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response;

import java.time.LocalDateTime;

public record MaratonResponse(
        Long id,
        String nombre,
        String descripcion,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        LocalDateTime fechaCreacion
) {}
