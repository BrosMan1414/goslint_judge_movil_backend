package com.goslint_judge_movil_backend.goslint_judge_movil_backend.config;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Maraton;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Problema;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.MaratonRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.ProblemaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seed inicial para cargar 10 problemas de Codeforces con metadata mínima.
 * Se ejecuta solo si la tabla problemas está vacía para evitar duplicados.
 */
@Component
@RequiredArgsConstructor
@Profile("prod") // Ajusta perfiles según tu necesidad; quita si quieres siempre.
public class CodeforcesSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CodeforcesSeedRunner.class);

    private final ProblemaRepository problemaRepository;
    private final MaratonRepository maratonRepository;

    @Override
    public void run(String... args) {
        if (problemaRepository.count() > 0) {
            log.info("Problemas ya existentes, no se ejecuta seed Codeforces.");
            return;
        }
        // Crear (o reutilizar) una maratón dummy para asociar los problemas.
        Maraton maraton = maratonRepository.findAll().stream().findFirst().orElseGet(() -> {
            Maraton m = Maraton.builder()
                    .nombre("Codeforces Demo")
                    .descripcion("Maratón de problemas importados de Codeforces para demo IA")
                    .build();
            return maratonRepository.save(m);
        });

        record P(String externalId, String title, Integer rating, List<String> tags, String url, String summary) {}

        List<P> data = List.of(
                new P("4A", "Watermelon", 800, List.of("brute force","math"), "https://codeforces.com/problemset/problem/4/A", "Determinar si un número par mayor a 2 se puede dividir en dos partes pares."),
                new P("71A", "Way Too Long Words", 800, List.of("strings"), "https://codeforces.com/problemset/problem/71/A", "Abreviar palabras largas conservando primera y última letra."),
                new P("231A", "Team", 800, List.of("brute force","greedy"), "https://codeforces.com/problemset/problem/231/A", "Contar problemas que al menos dos integrantes quieren resolver."),
                new P("282A", "Bit++", 800, List.of("implementation"), "https://codeforces.com/problemset/problem/282/A", "Simular operaciones incrementales/decrementales sobre X."),
                new P("1A", "Theatre Square", 1000, List.of("math"), "https://codeforces.com/problemset/problem/1/A", "Calcular número de baldosas para cubrir un área LxW con baldosas AxA."),
                new P("158A", "Next Round", 800, List.of("implementation"), "https://codeforces.com/problemset/problem/158/A", "Determinar cuantos avanzan según puntuación mínima y orden."),
                new P("50A", "Domino piling", 800, List.of("greedy","math"), "https://codeforces.com/problemset/problem/50/A", "Máximo número de fichas de dominó que caben en tablero MxN."),
                new P("263A", "Beautiful Matrix", 800, List.of("implementation"), "https://codeforces.com/problemset/problem/263/A", "Distancia de Manhattan para mover el '1' al centro."),
                new P("112A", "Petya and Strings", 800, List.of("implementation","strings"), "https://codeforces.com/problemset/problem/112/A", "Comparar dos strings ignorando mayúsculas."),
                new P("236A", "Boy or Girl", 800, List.of("brute force","implementation","strings"), "https://codeforces.com/problemset/problem/236/A", "Decidir un mensaje según número de caracteres distintos en el nombre.")
        );

        data.forEach(p -> {
            Problema problema = Problema.builder()
                    .maraton(maraton)
                    .titulo(p.title())
                    .enunciado(p.summary()) // usando summary como 'enunciado' simplificado
                    .limiteTiempo(2000) // valores genéricos
                    .limiteMemoria(256) // MB
                    .sourcePlatform("codeforces")
                    .externalId(p.externalId())
                    .sourceUrl(p.url())
                    .difficulty(p.rating())
                    .tags(toJsonArray(p.tags()))
                    .summary(p.summary())
                    .build();
            problemaRepository.save(problema);
        });
        log.info("Seed Codeforces: insertados {} problemas.", data.size());
    }

    private String toJsonArray(List<String> tags) {
        return tags.stream().map(t -> "\"" + t + "\"").reduce((a,b) -> a + "," + b).map(s -> "[" + s + "]").orElse("[]");
    }
}
