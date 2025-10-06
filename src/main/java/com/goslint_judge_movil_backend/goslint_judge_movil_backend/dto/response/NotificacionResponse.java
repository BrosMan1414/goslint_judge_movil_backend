package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response;

import java.time.LocalDateTime;

public record NotificacionResponse(
        Long id,
        Long equipoId,
        String mensaje,
        Boolean leido,
        LocalDateTime fecha
) {}
