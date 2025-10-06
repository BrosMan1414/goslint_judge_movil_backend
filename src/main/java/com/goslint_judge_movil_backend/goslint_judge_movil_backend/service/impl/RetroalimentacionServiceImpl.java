package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.impl;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.RetroalimentacionRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.RetroalimentacionResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.exception.NotFoundException;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Envio;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Retroalimentacion;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EnvioRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.RetroalimentacionRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.RetroalimentacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RetroalimentacionServiceImpl implements RetroalimentacionService {

    private final RetroalimentacionRepository retroRepo;
    private final EnvioRepository envioRepository;

    @Override
    public RetroalimentacionResponse create(RetroalimentacionRequest r) {
        Envio envio = envioRepository.findById(r.envioId())
                .orElseThrow(() -> new NotFoundException("Envío no encontrado id=" + r.envioId()));
        Retroalimentacion retro = Retroalimentacion.builder()
                .envio(envio)
                .tipo(r.tipo())
                .comentario(r.comentario())
                .build();
        return toResponse(retroRepo.save(retro));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RetroalimentacionResponse> findByEnvio(Long envioId) {
        return retroRepo.findByEnvio_Id(envioId).stream().map(this::toResponse).toList();
    }

    @Override
    public void delete(Long id) {
        if(!retroRepo.existsById(id))
            throw new NotFoundException("Retroalimentación no encontrada id=" + id);
        retroRepo.deleteById(id);
    }

    private RetroalimentacionResponse toResponse(Retroalimentacion r) {
        return new RetroalimentacionResponse(r.getId(), r.getEnvio().getId(), r.getTipo(), r.getComentario(), r.getFecha());
    }
}
