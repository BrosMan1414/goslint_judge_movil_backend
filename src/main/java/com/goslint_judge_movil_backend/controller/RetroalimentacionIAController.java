package com.goslint_judge_movil_backend.controller;

import com.goslint_judge_movil_backend.service.RetroalimentacionIAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/retroalimentacion/ia")
@RequiredArgsConstructor
public class RetroalimentacionIAController {

    private final RetroalimentacionIAService retroalimentacionIAService;

    @PostMapping("/{envioId}")
    public ResponseEntity<String> generarRetroalimentacion(@PathVariable Long envioId) {
        String retroalimentacion = retroalimentacionIAService.generarRetroalimentacionIA(envioId);
        return ResponseEntity.ok(retroalimentacion);
    }
}