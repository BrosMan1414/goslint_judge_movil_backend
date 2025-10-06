package com.goslint_judge_movil_backend.goslint_judge_movil_backend.controller;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.EnvioRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.EnvioResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.EnvioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    @PostMapping
    public ResponseEntity<EnvioResponse> create(@Valid @RequestBody EnvioRequest r) {
        EnvioResponse resp = envioService.create(r);
        return ResponseEntity.created(URI.create("/api/envios/" + resp.id())).body(resp);
    }

    @GetMapping
    public List<EnvioResponse> all() { return envioService.findAll(); }

    @GetMapping("/{id}")
    public EnvioResponse one(@PathVariable Long id) { return envioService.findById(id); }

    @PutMapping("/{id}")
    public EnvioResponse update(@PathVariable Long id, @Valid @RequestBody EnvioRequest r) { return envioService.update(id, r); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { envioService.delete(id); return ResponseEntity.noContent().build(); }
}
