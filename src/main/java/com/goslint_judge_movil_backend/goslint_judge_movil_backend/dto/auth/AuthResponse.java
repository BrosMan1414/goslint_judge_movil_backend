package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.auth;

public record AuthResponse(
        Long id,
        String nombre,
        String email,
        String token
) {}
