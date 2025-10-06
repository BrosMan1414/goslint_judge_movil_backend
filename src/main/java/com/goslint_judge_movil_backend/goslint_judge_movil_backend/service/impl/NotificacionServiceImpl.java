package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.impl;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.NotificacionRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.NotificacionResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.exception.NotFoundException;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Equipo;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Notificacion;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EquipoRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.NotificacionRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final EquipoRepository equipoRepository;

    @Override
    public NotificacionResponse create(NotificacionRequest r) {
        Equipo e = equipoRepository.findById(r.equipoId())
                .orElseThrow(() -> new NotFoundException("Equipo no encontrado id=" + r.equipoId()));
        Notificacion n = Notificacion.builder()
                .equipo(e)
                .mensaje(r.mensaje())
                .build();
        return toResponse(notificacionRepository.save(n));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponse> findNoLeidas(Long equipoId) {
        return notificacionRepository.findByEquipo_IdAndLeidoFalse(equipoId).stream().map(this::toResponse).toList();
    }

    @Override
    public NotificacionResponse marcarLeida(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notificación no encontrada id=" + id));
        n.setLeido(true);
        return toResponse(n);
    }

    @Override
    public void delete(Long id) {
        if(!notificacionRepository.existsById(id))
            throw new NotFoundException("Notificación no encontrada id=" + id);
        notificacionRepository.deleteById(id);
    }

    private NotificacionResponse toResponse(Notificacion n) {
        return new NotificacionResponse(n.getId(), n.getEquipo().getId(), n.getMensaje(), n.getLeido(), n.getFecha());
    }
}
