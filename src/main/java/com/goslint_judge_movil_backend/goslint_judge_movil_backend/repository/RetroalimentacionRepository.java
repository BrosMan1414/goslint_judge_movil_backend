package com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Retroalimentacion;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.TipoRetro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RetroalimentacionRepository extends JpaRepository<Retroalimentacion, Long> {
    List<Retroalimentacion> findByEnvio_Id(Long envioId);

    Optional<Retroalimentacion> findTopByEnvio_IdAndTipoOrderByFechaDesc(Long envioId, TipoRetro tipo);

    List<Retroalimentacion> findTop5ByEnvio_Equipo_IdAndEnvio_Problema_Maraton_IdAndTipoOrderByFechaDesc(Long equipoId, Long maratonId, TipoRetro tipo);
}
