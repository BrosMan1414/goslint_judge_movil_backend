package com.goslint_judge_movil_backend.goslint_judge_movil_backend.controller;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.ProblemaRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.ProblemaResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.ProblemaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/problemas")
@RequiredArgsConstructor
public class ProblemaController {

    private final ProblemaService problemaService;

    @PostMapping
    public ResponseEntity<ProblemaResponse> create(@Valid @RequestBody ProblemaRequest r) {
        ProblemaResponse resp = problemaService.create(r);
        return ResponseEntity.created(URI.create("/api/problemas/" + resp.id())).body(resp);
    }

    @GetMapping
    public List<ProblemaResponse> all() { return problemaService.findAll(); }

    @GetMapping("/{id}")
    public ProblemaResponse one(@PathVariable Long id) { return problemaService.findById(id); }

    @PutMapping("/{id}")
    public ProblemaResponse update(@PathVariable Long id, @Valid @RequestBody ProblemaRequest r) { return problemaService.update(id, r); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { problemaService.delete(id); return ResponseEntity.noContent().build(); }
}
