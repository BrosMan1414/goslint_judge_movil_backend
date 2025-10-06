package com.goslint_judge_movil_backend.goslint_judge_movil_backend.controller;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.MaratonRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.MaratonResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.MaratonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/maratones")
@RequiredArgsConstructor
public class MaratonController {

    private final MaratonService maratonService;

    @PostMapping
    public ResponseEntity<MaratonResponse> create(@Valid @RequestBody MaratonRequest r) {
        MaratonResponse resp = maratonService.create(r);
        return ResponseEntity.created(URI.create("/api/maratones/" + resp.id())).body(resp);
    }

    @GetMapping
    public List<MaratonResponse> all() { return maratonService.findAll(); }

    @GetMapping("/{id}")
    public MaratonResponse one(@PathVariable Long id) { return maratonService.findById(id); }

    @PutMapping("/{id}")
    public MaratonResponse update(@PathVariable Long id, @Valid @RequestBody MaratonRequest r) { return maratonService.update(id, r); }

    @PostMapping("/{maratonId}/inscribir/{equipoId}")
    public ResponseEntity<Void> inscribir(@PathVariable Long maratonId, @PathVariable Long equipoId) {
        maratonService.inscribirEquipo(maratonId, equipoId); return ResponseEntity.noContent().build(); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { maratonService.delete(id); return ResponseEntity.noContent().build(); }
}
