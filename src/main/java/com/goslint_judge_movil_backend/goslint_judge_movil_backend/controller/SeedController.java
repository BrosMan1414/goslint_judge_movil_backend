package com.goslint_judge_movil_backend.goslint_judge_movil_backend.controller;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.SeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/seed")
@CrossOrigin
public class SeedController {

    private final SeedService seedService;

    public SeedController(SeedService seedService) { this.seedService = seedService; }

    @PostMapping("/full")
    public ResponseEntity<Map<String,Object>> runFull() {
        return ResponseEntity.ok(seedService.runFullSeed());
    }
}
