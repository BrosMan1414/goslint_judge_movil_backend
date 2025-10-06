package com.goslint_judge_movil_backend.goslint_judge_movil_backend.controller;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.NotificacionRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.NotificacionResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @PostMapping
    public ResponseEntity<NotificacionResponse> create(@Valid @RequestBody NotificacionRequest r) {
        NotificacionResponse resp = notificacionService.create(r);
        return ResponseEntity.created(URI.create("/api/notificaciones/" + resp.id())).body(resp);
    }

    @GetMapping("/equipo/{equipoId}/noleidas")
    public List<NotificacionResponse> noLeidas(@PathVariable Long equipoId) { return notificacionService.findNoLeidas(equipoId); }

    @PostMapping("/{id}/marcar-leida")
    public NotificacionResponse marcarLeida(@PathVariable Long id) { return notificacionService.marcarLeida(id); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { notificacionService.delete(id); return ResponseEntity.noContent().build(); }
}
