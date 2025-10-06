package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.TipoRetro;
import jakarta.validation.constraints.*;

public record RetroalimentacionRequest(
        @NotNull Long envioId,
        @NotNull TipoRetro tipo,
        @NotBlank String comentario
) {}
