package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response;

import java.time.LocalDateTime;

public record ProblemaResponse(
        Long id,
        Long maratonId,
        String titulo,
        String enunciado,
        Integer limiteTiempo,
        Integer limiteMemoria,
        LocalDateTime fechaCreacion
) {}
