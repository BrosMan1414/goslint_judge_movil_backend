package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request;

import jakarta.validation.constraints.*;

public record NotificacionRequest(
        @NotNull Long equipoId,
        @NotBlank String mensaje
) {}
