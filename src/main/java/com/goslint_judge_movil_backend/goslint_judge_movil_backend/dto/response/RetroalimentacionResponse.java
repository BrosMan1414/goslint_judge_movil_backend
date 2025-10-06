package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.TipoRetro;
import java.time.LocalDateTime;

public record RetroalimentacionResponse(
        Long id,
        Long envioId,
        TipoRetro tipo,
        String comentario,
        LocalDateTime fecha
) {}
