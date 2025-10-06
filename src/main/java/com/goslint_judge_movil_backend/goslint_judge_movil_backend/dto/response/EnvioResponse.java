package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.ResultadoEnvio;
import java.time.LocalDateTime;

public record EnvioResponse(
        Long id,
        Long equipoId,
        Long problemaId,
        String lenguaje,
        String codigo,
        ResultadoEnvio resultado,
        Double tiempoEjecucion,
        Integer memoriaUsada,
        LocalDateTime fecha
) {}
