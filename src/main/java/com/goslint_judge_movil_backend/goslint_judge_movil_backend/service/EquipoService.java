package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.EquipoRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.EquipoResponse;
import java.util.List;

public interface EquipoService {
    EquipoResponse create(EquipoRequest request);
    List<EquipoResponse> findAll();
    EquipoResponse findById(Long id);
    EquipoResponse update(Long id, EquipoRequest request);
    void delete(Long id);
}
