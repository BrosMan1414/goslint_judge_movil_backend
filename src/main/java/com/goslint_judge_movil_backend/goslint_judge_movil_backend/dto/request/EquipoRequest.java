package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request;

import jakarta.validation.constraints.*;

public record EquipoRequest(
        @NotBlank String nombre,
        @Pattern(regexp = ".+@.+", message = "Debe contener @") @NotBlank String emailContacto,
        @NotBlank String password
) {}
