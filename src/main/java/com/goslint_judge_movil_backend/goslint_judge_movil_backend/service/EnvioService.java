package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.EnvioRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.EnvioResponse;
import java.util.List;

public interface EnvioService {
    EnvioResponse create(EnvioRequest r);
    List<EnvioResponse> findAll();
    EnvioResponse findById(Long id);
    EnvioResponse update(Long id, EnvioRequest r);
    void delete(Long id);
}
