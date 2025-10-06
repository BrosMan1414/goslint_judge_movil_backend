package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.RetroalimentacionRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.RetroalimentacionResponse;
import java.util.List;

public interface RetroalimentacionService {
    RetroalimentacionResponse create(RetroalimentacionRequest r);
    List<RetroalimentacionResponse> findByEnvio(Long envioId);
    void delete(Long id);
}
