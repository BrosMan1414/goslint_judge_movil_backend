package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.auth.LoginRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.auth.AuthResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.EquipoRequest;

public interface AuthService {
    AuthResponse register(EquipoRequest request);
    AuthResponse login(LoginRequest request);
}
