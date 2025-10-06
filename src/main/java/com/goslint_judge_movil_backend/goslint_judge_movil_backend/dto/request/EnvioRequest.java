package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request;

import jakarta.validation.constraints.*;

public record EnvioRequest(
        @NotNull Long equipoId,
        @NotNull Long problemaId,
        @NotBlank String lenguaje,
        @NotBlank String codigo
) {}
