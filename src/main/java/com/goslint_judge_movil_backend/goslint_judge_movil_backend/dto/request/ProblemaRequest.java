package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request;

import jakarta.validation.constraints.*;

public record ProblemaRequest(
        @NotNull Long maratonId,
        @NotBlank String titulo,
        @NotBlank String enunciado,
        @NotNull Integer limiteTiempo,
        @NotNull Integer limiteMemoria
) {}
