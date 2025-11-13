package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.chat.ChatTurnResponse;
import java.util.List;

public interface ChatService {
    ChatTurnResponse enviarMensaje(Long equipoId, Long maratonId, String mensajeUsuario);
    List<ChatTurnResponse> historial(Long equipoId, Long maratonId);
}
