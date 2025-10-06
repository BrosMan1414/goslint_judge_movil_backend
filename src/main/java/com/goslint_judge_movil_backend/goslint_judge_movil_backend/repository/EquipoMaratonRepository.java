package com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.EquipoMaraton;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipoMaratonRepository extends JpaRepository<EquipoMaraton, Long> {
    boolean existsByEquipo_IdAndMaraton_Id(Long equipoId, Long maratonId);
    List<EquipoMaraton> findByEquipo_Id(Long equipoId);
    List<EquipoMaraton> findByMaraton_Id(Long maratonId);
}
