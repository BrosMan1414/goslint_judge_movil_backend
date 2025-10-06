package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response;

import java.time.LocalDateTime;

public record EquipoResponse(
        Long id,
        String nombre,
        String emailContacto,
        Integer puntaje,
        LocalDateTime fechaCreacion
) {}
