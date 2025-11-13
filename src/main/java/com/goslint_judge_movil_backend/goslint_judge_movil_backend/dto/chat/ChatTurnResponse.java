package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatTurnResponse {
    private Long id;
    private String mensajeUsuario;
    private String respuestaIA;
    private LocalDateTime fecha;
}
