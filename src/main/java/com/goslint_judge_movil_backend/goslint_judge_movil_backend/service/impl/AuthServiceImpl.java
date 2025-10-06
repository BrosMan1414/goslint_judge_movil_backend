package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.impl;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.auth.AuthResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.auth.LoginRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.EquipoRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.exception.NotFoundException;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Equipo;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EquipoRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final EquipoRepository equipoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(EquipoRequest request) {
        // validar email único
    if(equipoRepository.findAll().stream().anyMatch(e -> e.getEmailContacto().equalsIgnoreCase(request.emailContacto()))) {
            throw new IllegalArgumentException("Email ya registrado");
        }
        Equipo equipo = Equipo.builder()
                .nombre(request.nombre())
        .emailContacto(request.emailContacto())
        .passwordHash(passwordEncoder.encode(request.password()))
                .build();
        Equipo saved = equipoRepository.save(equipo);
    return new AuthResponse(saved.getId(), saved.getNombre(), saved.getEmailContacto(), null); // token null por ahora
    }

    @Override
    public AuthResponse login(LoginRequest request) {
    Equipo equipo = equipoRepository.findAll().stream()
        .filter(e -> e.getEmailContacto().equalsIgnoreCase(request.email()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Credenciales inválidas"));
    if(!passwordEncoder.matches(request.password(), equipo.getPasswordHash())) {
            throw new NotFoundException("Credenciales inválidas");
        }
    return new AuthResponse(equipo.getId(), equipo.getNombre(), equipo.getEmailContacto(), null);
    }
}
