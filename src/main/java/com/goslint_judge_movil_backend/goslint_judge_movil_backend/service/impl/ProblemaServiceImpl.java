package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.impl;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.ProblemaRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.ProblemaResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.exception.NotFoundException;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Maraton;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Problema;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.MaratonRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.ProblemaRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.ProblemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemaServiceImpl implements ProblemaService {

    private final ProblemaRepository problemaRepository;
    private final MaratonRepository maratonRepository;

    @Override
    public ProblemaResponse create(ProblemaRequest r) {
        Maraton m = maratonRepository.findById(r.maratonId())
                .orElseThrow(() -> new NotFoundException("Maratón no encontrado id=" + r.maratonId()));
        Problema p = Problema.builder()
                .maraton(m)
                .titulo(r.titulo())
                .enunciado(r.enunciado())
                .limiteTiempo(r.limiteTiempo())
                .limiteMemoria(r.limiteMemoria())
                .build();
        return toResponse(problemaRepository.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemaResponse> findAll() {
        return problemaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemaResponse findById(Long id) {
        return problemaRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Problema no encontrado id=" + id));
    }

    @Override
    public ProblemaResponse update(Long id, ProblemaRequest r) {
        Problema p = problemaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Problema no encontrado id=" + id));
        if (!p.getMaraton().getId().equals(r.maratonId())) {
            Maraton m = maratonRepository.findById(r.maratonId())
                    .orElseThrow(() -> new NotFoundException("Maratón no encontrado id=" + r.maratonId()));
            p.setMaraton(m);
        }
        p.setTitulo(r.titulo());
        p.setEnunciado(r.enunciado());
        p.setLimiteTiempo(r.limiteTiempo());
        p.setLimiteMemoria(r.limiteMemoria());
        return toResponse(p);
    }

    @Override
    public void delete(Long id) {
        if (!problemaRepository.existsById(id))
            throw new NotFoundException("Problema no encontrado id=" + id);
        problemaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemaResponse> findByMaraton(Long maratonId) {
        return problemaRepository.findByMaraton_Id(maratonId).stream().map(this::toResponse).toList();
    }

    private ProblemaResponse toResponse(Problema p) {
        return new ProblemaResponse(p.getId(), p.getMaraton().getId(), p.getTitulo(), p.getEnunciado(), p.getLimiteTiempo(), p.getLimiteMemoria(), p.getFechaCreacion());
    }
}
