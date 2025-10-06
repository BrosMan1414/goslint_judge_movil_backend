package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.MaratonRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.MaratonResponse;
import java.util.List;

public interface MaratonService {
    MaratonResponse create(MaratonRequest r);
    List<MaratonResponse> findAll();
    MaratonResponse findById(Long id);
    MaratonResponse update(Long id, MaratonRequest r);
    void delete(Long id);
    void inscribirEquipo(Long maratonId, Long equipoId);
}
