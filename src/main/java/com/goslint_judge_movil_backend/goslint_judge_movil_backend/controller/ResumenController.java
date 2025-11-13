package com.goslint_judge_movil_backend.goslint_judge_movil_backend.controller;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.ResumenMaratonResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.ResumenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resumen")
@CrossOrigin
public class ResumenController {

    private final ResumenService resumenService;

    public ResumenController(ResumenService resumenService) {
        this.resumenService = resumenService;
    }

    @GetMapping("/equipos/{equipoId}/maratones/{maratonId}")
    public ResponseEntity<ResumenMaratonResponse> obtener(@PathVariable Long equipoId,
                                                          @PathVariable Long maratonId) {
        return ResponseEntity.ok(resumenService.obtenerResumen(equipoId, maratonId));
    }
}
