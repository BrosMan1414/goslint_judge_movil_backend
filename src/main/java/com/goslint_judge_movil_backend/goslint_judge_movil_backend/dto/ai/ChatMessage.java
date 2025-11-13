package com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String role;    // system | user | assistant
    private String content; // texto del mensaje
}
