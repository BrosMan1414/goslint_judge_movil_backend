package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class ResumenMaratonResponse {
    private Long equipoId;
    private Long maratonId;
    private int totalProblemas;
    private int totalEnvios;
    private int aceptados;
    private double porcentajeAceptados;
    private int intentosFallidos;
    private List<FeedbackItem> recientesFeedback; // últimos feedback IA

    @Getter
    @Setter
    @Builder
    public static class FeedbackItem {
        private Long envioId;
        private Long problemaId;
        private String problemaTitulo;
        private String comentario;
    }
}
