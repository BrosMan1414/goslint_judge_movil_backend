package com.goslint_judge_movil_backend.goslint_judge_movil_backend.controller;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.chat.ChatTurnRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.chat.ChatTurnResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/maraton/{maratonId}/equipo/{equipoId}")
    public ResponseEntity<ChatTurnResponse> enviar(@PathVariable Long maratonId,
                                                   @PathVariable Long equipoId,
                                                   @RequestBody ChatTurnRequest request) {
        return ResponseEntity.ok(chatService.enviarMensaje(equipoId, maratonId, request.getMensaje()));
    }

    @GetMapping("/maraton/{maratonId}/equipo/{equipoId}")
    public ResponseEntity<List<ChatTurnResponse>> historial(@PathVariable Long maratonId,
                                                            @PathVariable Long equipoId) {
        return ResponseEntity.ok(chatService.historial(equipoId, maratonId));
    }
}
