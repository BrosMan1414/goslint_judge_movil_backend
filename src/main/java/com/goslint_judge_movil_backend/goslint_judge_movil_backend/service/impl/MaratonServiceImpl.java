package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.impl;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.MaratonRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.MaratonResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.exception.NotFoundException;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Equipo;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.EquipoMaraton;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Maraton;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EquipoMaratonRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EquipoRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.MaratonRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.MaratonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MaratonServiceImpl implements MaratonService {

    private final MaratonRepository maratonRepository;
    private final EquipoRepository equipoRepository;
    private final EquipoMaratonRepository equipoMaratonRepository;

    @Override
    public MaratonResponse create(MaratonRequest r) {
        Maraton m = Maraton.builder()
                .nombre(r.nombre())
                .descripcion(r.descripcion())
                .fechaInicio(r.fechaInicio())
                .fechaFin(r.fechaFin())
                .build();
        return toResponse(maratonRepository.save(m));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaratonResponse> findAll() {
        return maratonRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MaratonResponse findById(Long id) {
        return maratonRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Maratón no encontrado id=" + id));
    }

    @Override
    public MaratonResponse update(Long id, MaratonRequest r) {
        Maraton m = maratonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Maratón no encontrado id=" + id));
        m.setNombre(r.nombre());
        m.setDescripcion(r.descripcion());
        m.setFechaInicio(r.fechaInicio());
        m.setFechaFin(r.fechaFin());
        return toResponse(m);
    }

    @Override
    public void delete(Long id) {
        if(!maratonRepository.existsById(id))
            throw new NotFoundException("Maratón no encontrado id=" + id);
        maratonRepository.deleteById(id);
    }

    @Override
    public void inscribirEquipo(Long maratonId, Long equipoId) {
        if (equipoMaratonRepository.existsByEquipo_IdAndMaraton_Id(equipoId, maratonId)) {
            return; // ya inscrito
        }
        Maraton m = maratonRepository.findById(maratonId)
                .orElseThrow(() -> new NotFoundException("Maratón no encontrado id=" + maratonId));
        Equipo e = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new NotFoundException("Equipo no encontrado id=" + equipoId));
        EquipoMaraton em = EquipoMaraton.builder().maraton(m).equipo(e).build();
        equipoMaratonRepository.save(em);
    }

    private MaratonResponse toResponse(Maraton m) {
        return new MaratonResponse(m.getId(), m.getNombre(), m.getDescripcion(), m.getFechaInicio(), m.getFechaFin(), m.getFechaCreacion());
    }
}
