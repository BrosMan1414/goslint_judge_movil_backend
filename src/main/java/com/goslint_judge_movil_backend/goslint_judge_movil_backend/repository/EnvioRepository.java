package com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnvioRepository extends JpaRepository<Envio, Long> {
    List<Envio> findByEquipo_Id(Long equipoId);
    List<Envio> findByProblema_Id(Long problemaId);
    List<Envio> findByEquipo_IdAndProblema_Maraton_Id(Long equipoId, Long maratonId);
}
