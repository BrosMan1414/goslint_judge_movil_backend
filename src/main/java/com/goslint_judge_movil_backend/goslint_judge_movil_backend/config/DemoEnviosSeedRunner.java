package com.goslint_judge_movil_backend.goslint_judge_movil_backend.config;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.*;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.ResultadoEnvio;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.*;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.RetroalimentacionIAService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Random;

/**
 * Seed que crea un equipo demo y 10 envíos (4 Accepted, 6 fallidos) sobre los problemas Codeforces.
 * Luego genera retroalimentación IA para cada envío.
 */
@Component
@RequiredArgsConstructor
@Profile("prod") // Ajusta si quieres ejecutarlo también en dev
public class DemoEnviosSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoEnviosSeedRunner.class);

    private final ProblemaRepository problemaRepository;
    private final EnvioRepository envioRepository;
    private final EquipoRepository equipoRepository;
    private final RetroalimentacionIAService iaService;

    @Override
    public void run(String... args) {
        if (envioRepository.count() > 0) {
            log.info("Ya existen envíos, no se ejecuta DemoEnviosSeedRunner.");
            return;
        }

        List<Problema> problemas = problemaRepository.findAll();
        if (problemas.size() < 10) {
            log.warn("Se esperaban 10 problemas, encontrados: {}. Seed abortado.", problemas.size());
            return;
        }

        // Crear equipo demo
        Equipo equipo = equipoRepository.findByEmailContacto("demo@equipo.com").orElseGet(() -> {
            Equipo e = Equipo.builder()
                    .nombre("EquipoDemoCF")
                    .emailContacto("demo@equipo.com")
                    // Password ya cifrado debería ir aquí; para demo guardamos hash ficticio
                    .passwordHash("demo-hash")
                    .puntaje(0)
                    .build();
            return equipoRepository.save(e);
        });

        // Distribución de resultados (puedes ajustar): 0,1,3, etc.
    ResultadoEnvio[] resultados = new ResultadoEnvio[]{
        // Primeros 4 Accepted como solicitaste
        ResultadoEnvio.Accepted, // 4A
        ResultadoEnvio.Accepted, // 71A
        ResultadoEnvio.Accepted, // 231A
        ResultadoEnvio.Accepted, // 282A
        // 6 fallidos variados
        ResultadoEnvio.TimeLimitExceeded, // 1A
        ResultadoEnvio.WrongAnswer,       // 158A
        ResultadoEnvio.RuntimeError,      // 50A
        ResultadoEnvio.WrongAnswer,       // 263A
        ResultadoEnvio.CompilationError,  // 112A
        ResultadoEnvio.WrongAnswer        // 236A
    };

        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            Problema p = problemas.get(i);
            Envio envio = Envio.builder()
                    .equipo(equipo)
                    .problema(p)
                    .lenguaje("java")
                    .codigo("// codigo de ejemplo enviado para problema " + p.getExternalId())
                    .resultado(resultados[i])
                    .tiempoEjecucion(resultados[i] == ResultadoEnvio.Accepted ? 0.5 + rnd.nextDouble() * 0.5 : 1.5 + rnd.nextDouble() * 3.0)
                    .memoriaUsada(resultados[i] == ResultadoEnvio.Accepted ? 32 + rnd.nextInt(32) : 64 + rnd.nextInt(128))
                    .build();
            envio = envioRepository.save(envio);
            try {
                String retro = iaService.generarRetroalimentacionIA(envio.getId());
                log.info("Retro IA generada para envio {} -> {}", envio.getId(), retro != null ? retro.substring(0, Math.min(60, retro.length())) + "..." : "(vacía)");
            } catch (Exception e) {
                log.warn("Fallo generando retro IA para envio {}: {}", envio.getId(), e.getMessage());
            }
        }
        log.info("Seed demo envíos completado.");
    }
}
