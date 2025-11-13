package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.impl;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.*;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.ResultadoEnvio;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.*;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.RetroalimentacionIAService;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.SeedService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SeedServiceImpl implements SeedService {

    private final ProblemaRepository problemaRepository;
    private final MaratonRepository maratonRepository;
    private final EquipoRepository equipoRepository;
    private final EnvioRepository envioRepository;
    private final ChatTurnRepository chatTurnRepository;
    private final RetroalimentacionIAService iaService;

    public SeedServiceImpl(ProblemaRepository problemaRepository,
                           MaratonRepository maratonRepository,
                           EquipoRepository equipoRepository,
                           EnvioRepository envioRepository,
                           ChatTurnRepository chatTurnRepository,
                           RetroalimentacionIAService iaService) {
        this.problemaRepository = problemaRepository;
        this.maratonRepository = maratonRepository;
        this.equipoRepository = equipoRepository;
        this.envioRepository = envioRepository;
        this.chatTurnRepository = chatTurnRepository;
        this.iaService = iaService;
    }

    @Override
    public Map<String, Object> runFullSeed() {
        Map<String,Object> summary = new LinkedHashMap<>();

        // Maratón base
        Maraton maraton = maratonRepository.findAll().stream().findFirst().orElseGet(() -> {
            Maraton m = Maraton.builder().nombre("Codeforces Demo").descripcion("Maratón de problemas importados").build();
            return maratonRepository.save(m);
        });
        summary.put("maratonId", maraton.getId());

        // Problemas Codeforces si faltan
        if (problemaRepository.count() == 0) {
            record P(String externalId, String title, Integer rating, List<String> tags, String url, String summaryTxt) {}
            List<P> data = List.of(
                new P("4A","Watermelon",800,List.of("brute force","math"),"https://codeforces.com/problemset/problem/4/A","Dividir peso par en dos pares."),
                new P("71A","Way Too Long Words",800,List.of("strings"),"https://codeforces.com/problemset/problem/71/A","Abreviar palabras largas."),
                new P("231A","Team",800,List.of("brute force","greedy"),"https://codeforces.com/problemset/problem/231/A","Contar decisiones del equipo."),
                new P("282A","Bit++",800,List.of("implementation"),"https://codeforces.com/problemset/problem/282/A","Simular operaciones sobre X."),
                new P("1A","Theatre Square",1000,List.of("math"),"https://codeforces.com/problemset/problem/1/A","Calcular baldosas requeridas."),
                new P("158A","Next Round",800,List.of("implementation"),"https://codeforces.com/problemset/problem/158/A","Contar clasifican según puntuación."),
                new P("50A","Domino piling",800,List.of("greedy","math"),"https://codeforces.com/problemset/problem/50/A","Máximo dominós en tablero."),
                new P("263A","Beautiful Matrix",800,List.of("implementation"),"https://codeforces.com/problemset/problem/263/A","Distancia mover el '1' al centro."),
                new P("112A","Petya and Strings",800,List.of("implementation","strings"),"https://codeforces.com/problemset/problem/112/A","Comparar sin case sensitivity."),
                new P("236A","Boy or Girl",800,List.of("implementation","strings"),"https://codeforces.com/problemset/problem/236/A","Decidir mensaje según caracteres únicos.")
            );
            data.forEach(p -> {
                Problema problema = Problema.builder()
                        .maraton(maraton)
                        .titulo(p.title())
                        .enunciado(p.summaryTxt())
                        .limiteTiempo(2000)
                        .limiteMemoria(256)
                        .sourcePlatform("codeforces")
                        .externalId(p.externalId())
                        .sourceUrl(p.url())
                        .difficulty(p.rating())
                        .tags(toJsonArray(p.tags()))
                        .summary(p.summaryTxt())
                        .build();
                problemaRepository.save(problema);
            });
        }
        summary.put("problemas", problemaRepository.count());

        // Equipo demo
        Equipo equipo = equipoRepository.findByEmailContacto("demo@equipo.com").orElseGet(() -> equipoRepository.save(
                Equipo.builder().nombre("EquipoDemoCF").emailContacto("demo@equipo.com").passwordHash("demo-hash").puntaje(0).build()
        ));
        summary.put("equipoId", equipo.getId());

        // Envíos y retro IA
        if (envioRepository.count() == 0) {
            List<Problema> problemas = problemaRepository.findAll();
            ResultadoEnvio[] resultados = new ResultadoEnvio[]{
                    ResultadoEnvio.Accepted,ResultadoEnvio.Accepted,ResultadoEnvio.Accepted,ResultadoEnvio.Accepted,
                    ResultadoEnvio.TimeLimitExceeded,ResultadoEnvio.WrongAnswer,ResultadoEnvio.RuntimeError,
                    ResultadoEnvio.WrongAnswer,ResultadoEnvio.CompilationError,ResultadoEnvio.WrongAnswer
            };
            Random rnd = new Random();
            for (int i=0;i<problemas.size() && i<10;i++) {
                Envio envio = Envio.builder()
                        .equipo(equipo)
                        .problema(problemas.get(i))
                        .lenguaje("java")
                        .codigo("// codigo demo " + problemas.get(i).getExternalId())
                        .resultado(resultados[i])
                        .tiempoEjecucion(resultados[i]==ResultadoEnvio.Accepted ? 0.5 + rnd.nextDouble()*0.5 : 1.5 + rnd.nextDouble()*3.0)
                        .memoriaUsada(resultados[i]==ResultadoEnvio.Accepted ? 40 + rnd.nextInt(20) : 80 + rnd.nextInt(60))
                        .build();
                envio = envioRepository.save(envio);
                try { iaService.generarRetroalimentacionIA(envio.getId()); } catch (Exception ignored) {}
            }
        }
        summary.put("envios", envioRepository.count());

        // Chat demo
        if (chatTurnRepository.count() == 0) {
            List<String[]> sample = List.of(
                    new String[]{"¿Cómo podemos mejorar nuestro ratio de aceptados?","- Resumen: base sólida, mejorar consistencia fallidos.\n- Sugerencias:\n  * Revisar patrones en fallidos.\n  * Reintentar el de menor dificultad.\n- Próximo paso: seleccionar un fallo y reintentar."},
                    new String[]{"¿Qué problema conviene atacar ahora?","- Resumen: alternar reintentos con nuevos fáciles.\n- Sugerencias:\n  * Reintentar 'Theatre Square'.\n  * Luego un nuevo de rating 800.\n- Próximo paso: preparar casos límite."}
            );
            Maraton m = maratonRepository.findAll().stream().findFirst().orElse(maraton);
            sample.forEach(p -> chatTurnRepository.save(ChatTurn.builder().equipo(equipo).maraton(m).userMessage(p[0]).aiResponse(p[1]).build()));
        }
        summary.put("chatTurns", chatTurnRepository.count());

        return summary;
    }

    private String toJsonArray(List<String> tags) {
        return tags.stream().map(t -> "\"" + t + "\"").reduce((a,b)->a+","+b).map(s->"["+s+"]").orElse("[]");
    }
}
