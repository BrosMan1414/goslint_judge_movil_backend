package com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Retroalimentacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RetroalimentacionRepository extends JpaRepository<Retroalimentacion, Long> {
    List<Retroalimentacion> findByEnvio_Id(Long envioId);
}
