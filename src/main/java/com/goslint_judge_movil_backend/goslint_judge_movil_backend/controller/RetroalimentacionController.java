package com.goslint_judge_movil_backend.goslint_judge_movil_backend.controller;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.RetroalimentacionRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.RetroalimentacionResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.RetroalimentacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/retroalimentaciones")
@RequiredArgsConstructor
public class RetroalimentacionController {

    private final RetroalimentacionService retroalimentacionService;

    @PostMapping
    public ResponseEntity<RetroalimentacionResponse> create(@Valid @RequestBody RetroalimentacionRequest r) {
        RetroalimentacionResponse resp = retroalimentacionService.create(r);
        return ResponseEntity.created(URI.create("/api/retroalimentaciones/" + resp.id())).body(resp);
    }

    @GetMapping("/envio/{envioId}")
    public List<RetroalimentacionResponse> byEnvio(@PathVariable Long envioId) { return retroalimentacionService.findByEnvio(envioId); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { retroalimentacionService.delete(id); return ResponseEntity.noContent().build(); }
}
