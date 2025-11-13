package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String model;
    private List<ChatMessage> messages;
    private Double temperature;
}
