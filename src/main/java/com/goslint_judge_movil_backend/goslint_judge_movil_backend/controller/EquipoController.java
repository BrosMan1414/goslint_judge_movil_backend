package com.goslint_judge_movil_backend.goslint_judge_movil_backend.controller;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.EquipoRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.EquipoResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.EquipoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
public class EquipoController {

    private final EquipoService equipoService;

    @PostMapping
    public ResponseEntity<EquipoResponse> create(@Valid @RequestBody EquipoRequest request) {
        EquipoResponse resp = equipoService.create(request);
        return ResponseEntity.created(URI.create("/api/equipos/" + resp.id())).body(resp);
    }

    @GetMapping
    public List<EquipoResponse> all() { return equipoService.findAll(); }

    @GetMapping("/{id}")
    public EquipoResponse one(@PathVariable Long id) { return equipoService.findById(id); }

    @PutMapping("/{id}")
    public EquipoResponse update(@PathVariable Long id, @Valid @RequestBody EquipoRequest request) { return equipoService.update(id, request); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { equipoService.delete(id); return ResponseEntity.noContent().build(); }
}
