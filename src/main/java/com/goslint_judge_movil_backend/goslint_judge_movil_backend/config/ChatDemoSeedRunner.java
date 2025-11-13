package com.goslint_judge_movil_backend.goslint_judge_movil_backend.config;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.ChatTurn;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Equipo;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Maraton;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.ChatTurnRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EquipoRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.MaratonRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seed opcional para crear un pequeño historial de chat de ejemplo.
 */
@Component
@RequiredArgsConstructor
@Profile("prod")
public class ChatDemoSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ChatDemoSeedRunner.class);

    private final ChatTurnRepository chatTurnRepository;
    private final EquipoRepository equipoRepository;
    private final MaratonRepository maratonRepository;

    @Override
    public void run(String... args) {
        if (chatTurnRepository.count() > 0) {
            log.info("Ya existen turnos de chat, no se ejecuta ChatDemoSeedRunner.");
            return;
        }

        Equipo equipo = equipoRepository.findByEmailContacto("demo@equipo.com").orElse(null);
        Maraton maraton = maratonRepository.findAll().stream().findFirst().orElse(null);
        if (equipo == null || maraton == null) {
            log.warn("No se encontró equipo demo o maratón para seed de chat.");
            return;
        }

        List<String[]> sample = List.of(
                new String[]{"¿Cómo podemos mejorar nuestro ratio de aceptados?", "- Resumen: tenéis base sólida, falta consistencia en fallidos.\n- Sugerencias:\n  * Revisar patrones en problemas fallidos.\n  * Priorizar reintentos en menor dificultad primero.\n- Próximo paso: elegir un problema fallido y reintentar con enfoque en optimización de entrada."},
                new String[]{"¿Qué problema conviene atacar ahora?", "- Resumen: conviene alternar entre fallidos y nuevos retos sencillos.\n- Sugerencias:\n  * Reintentar 'Theatre Square' para afianzar manejo de aritmética.\n  * Luego abordar uno nuevo de dificultad 800 para mantener ritmo.\n- Próximo paso: preparar implementación limpia y testear casos límite."}
        );

        sample.forEach(pair -> {
            ChatTurn turn = ChatTurn.builder()
                    .equipo(equipo)
                    .maraton(maraton)
                    .userMessage(pair[0])
                    .aiResponse(pair[1])
                    .build();
            chatTurnRepository.save(turn);
        });
        log.info("Seed de chat demo creado con {} turnos.", sample.size());
    }
}
