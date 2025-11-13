package com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.ChatTurn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatTurnRepository extends JpaRepository<ChatTurn, Long> {
    List<ChatTurn> findByEquipo_IdAndMaraton_IdOrderByCreatedAtAsc(Long equipoId, Long maratonId);
}
