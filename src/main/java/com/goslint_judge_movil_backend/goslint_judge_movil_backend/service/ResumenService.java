package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.ResumenMaratonResponse;

public interface ResumenService {
    ResumenMaratonResponse obtenerResumen(Long equipoId, Long maratonId);
}
