package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.impl;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.EnvioRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.EnvioResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.exception.NotFoundException;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Equipo;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Envio;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Problema;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EquipoRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EnvioRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.ProblemaRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.EnvioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;
    private final EquipoRepository equipoRepository;
    private final ProblemaRepository problemaRepository;

    @Override
    public EnvioResponse create(EnvioRequest r) {
        Equipo equipo = equipoRepository.findById(r.equipoId())
                .orElseThrow(() -> new NotFoundException("Equipo no encontrado id=" + r.equipoId()));
        Problema problema = problemaRepository.findById(r.problemaId())
                .orElseThrow(() -> new NotFoundException("Problema no encontrado id=" + r.problemaId()));
        Envio e = Envio.builder()
                .equipo(equipo)
                .problema(problema)
                .lenguaje(r.lenguaje())
                .codigo(r.codigo())
                .build();
        return toResponse(envioRepository.save(e));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponse> findAll() {
        return envioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponse findById(Long id) {
        return envioRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Envío no encontrado id=" + id));
    }

    @Override
    public EnvioResponse update(Long id, EnvioRequest r) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Envío no encontrado id=" + id));
        if(!envio.getEquipo().getId().equals(r.equipoId())) {
            Equipo equipo = equipoRepository.findById(r.equipoId())
                    .orElseThrow(() -> new NotFoundException("Equipo no encontrado id=" + r.equipoId()));
            envio.setEquipo(equipo);
        }
        if(!envio.getProblema().getId().equals(r.problemaId())) {
            Problema problema = problemaRepository.findById(r.problemaId())
                    .orElseThrow(() -> new NotFoundException("Problema no encontrado id=" + r.problemaId()));
            envio.setProblema(problema);
        }
        envio.setLenguaje(r.lenguaje());
        envio.setCodigo(r.codigo());
        // resultado, métricas se actualizarán en el juez posteriormente
        return toResponse(envio);
    }

    @Override
    public void delete(Long id) {
        if(!envioRepository.existsById(id))
            throw new NotFoundException("Envío no encontrado id=" + id);
        envioRepository.deleteById(id);
    }

    private EnvioResponse toResponse(Envio e) {
        return new EnvioResponse(e.getId(), e.getEquipo().getId(), e.getProblema().getId(), e.getLenguaje(), e.getCodigo(), e.getResultado(), e.getTiempoEjecucion(), e.getMemoriaUsada(), e.getFecha());
    }
}
