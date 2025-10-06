package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response;

import java.time.LocalDateTime;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}
