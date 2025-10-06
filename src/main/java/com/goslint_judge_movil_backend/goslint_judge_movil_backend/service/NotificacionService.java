package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.NotificacionRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.NotificacionResponse;
import java.util.List;

public interface NotificacionService {
    NotificacionResponse create(NotificacionRequest r);
    List<NotificacionResponse> findNoLeidas(Long equipoId);
    NotificacionResponse marcarLeida(Long id);
    void delete(Long id);
}
