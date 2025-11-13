package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.ai;

import lombok.Data;

import java.util.List;

@Data
public class ChatResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private ChatMessage message;
        private String finish_reason;
    }
}
