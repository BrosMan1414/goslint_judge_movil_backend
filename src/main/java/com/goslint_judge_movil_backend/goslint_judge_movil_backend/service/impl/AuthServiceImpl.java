package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.impl;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.auth.AuthResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.auth.LoginRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.EquipoRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.exception.NotFoundException;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Equipo;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EquipoRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.util.AES;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final EquipoRepository equipoRepository;



    @Override
    public AuthResponse register(EquipoRequest request) {
        // validar email único
        if(equipoRepository.findAll().stream().anyMatch(e -> e.getEmailContacto().equalsIgnoreCase(request.emailContacto()))) {
            throw new IllegalArgumentException("Email ya registrado");
        }
        // Clave AES fija (en producción, usar una configurable y segura)
        String aesKey = "0123456789abcdef";
        byte[] key = AES.prepareKey(aesKey);
        byte[] expandedKey = AES.expandKey(key);
        String encryptedPassword;
        try {
            byte[] cipher = AES.encryptECB(request.password().getBytes(), expandedKey);
            encryptedPassword = java.util.Base64.getEncoder().encodeToString(cipher);
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar la contraseña", e);
        }
        Equipo equipo = Equipo.builder()
                .nombre(request.nombre())
                .emailContacto(request.emailContacto())
                .passwordHash(encryptedPassword)
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
        // Cifrar el password recibido y comparar con el almacenado
        String aesKey = "0123456789abcdef";
        byte[] key = AES.prepareKey(aesKey);
        byte[] expandedKey = AES.expandKey(key);
        String encryptedPassword;
        try {
            byte[] cipher = AES.encryptECB(request.password().getBytes(), expandedKey);
            encryptedPassword = java.util.Base64.getEncoder().encodeToString(cipher);
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar la contraseña", e);
        }
        if(!encryptedPassword.equals(equipo.getPasswordHash())) {
            throw new NotFoundException("Credenciales inválidas");
        }
        return new AuthResponse(equipo.getId(), equipo.getNombre(), equipo.getEmailContacto(), null);
    }
}
