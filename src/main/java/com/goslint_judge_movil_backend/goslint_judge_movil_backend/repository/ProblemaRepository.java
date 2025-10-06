package com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Problema;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProblemaRepository extends JpaRepository<Problema, Long> {
    List<Problema> findByMaraton_Id(Long maratonId);
}
