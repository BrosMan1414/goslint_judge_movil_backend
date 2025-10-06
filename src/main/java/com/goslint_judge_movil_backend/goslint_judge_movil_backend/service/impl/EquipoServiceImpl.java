package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.impl;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.EquipoRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.EquipoResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.exception.NotFoundException;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Equipo;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EquipoRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.EquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;

    @Override
    public EquipoResponse create(EquipoRequest request) {
        Equipo e = Equipo.builder()
                .nombre(request.nombre())
                .emailContacto(request.emailContacto())
                .passwordHash(request.password())
                .build();
        return toResponse(equipoRepository.save(e));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipoResponse> findAll() {
        return equipoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EquipoResponse findById(Long id) {
        return equipoRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Equipo no encontrado id=" + id));
    }

    @Override
    public EquipoResponse update(Long id, EquipoRequest request) {
        Equipo e = equipoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipo no encontrado id=" + id));
        e.setNombre(request.nombre());
        e.setEmailContacto(request.emailContacto());
        e.setPasswordHash(request.password());
        return toResponse(e);
    }

    @Override
    public void delete(Long id) {
        if (!equipoRepository.existsById(id))
            throw new NotFoundException("Equipo no encontrado id=" + id);
        equipoRepository.deleteById(id);
    }

    private EquipoResponse toResponse(Equipo e) {
        return new EquipoResponse(e.getId(), e.getNombre(), e.getEmailContacto(), e.getPuntaje(), e.getFechaCreacion());
    }
}
