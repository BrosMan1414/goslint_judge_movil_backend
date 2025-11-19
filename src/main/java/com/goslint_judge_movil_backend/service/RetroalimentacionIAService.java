package com.goslint_judge_movil_backend.service;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Retroalimentacion;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Envio;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.TipoRetro;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EnvioRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.RetroalimentacionRepository;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.springframework.lang.NonNull;

@Service
public class RetroalimentacionIAService {

    @Autowired
    private WebClient openRouterWebClient;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private RetroalimentacionRepository retroalimentacionRepository;

    public String generarRetroalimentacionIA(@NonNull Long envioId) {
        Envio envio = envioRepository.findById(envioId).orElseThrow(() -> new IllegalArgumentException("El envío no existe."));

        String prompt = "Eres un asistente experto en programación competitiva, análisis de rendimiento y evaluación de envíos en maratones tipo ICPC/Competitive Programming.\r\n" + //
                        "\r\n" + //
                        "Tu función principal es proporcionar retroalimentación clara, útil y pedagógica sobre el desempeño de equipos o participantes, basándote en los datos de sus envíos, resultados, tiempo, memoria, errores y progreso dentro de una maratón.\r\n" + //
                        "\r\n" + //
                        "Sin embargo, también debes ser capaz de responder preguntas normales de conversación cuando el usuario no esté pidiendo retroalimentación técnica. Si el usuario hace una pregunta general, responde de manera natural.\r\n" + //
                        "\r\n" + //
                        "Instrucciones clave sobre tu comportamiento:\r\n" + //
                        "\r\n" + //
                        "1. **Cuando el usuario proporcione información de envíos, resultados o desempeño de una maratón**, analiza profunda y técnicamente:\r\n" + //
                        "   - causas probables de errores (WA, TLE, MLE, RTE, CE),\r\n" + //
                        "   - calidad de la solución,\r\n" + //
                        "   - eficiencia temporal y espacial,\r\n" + //
                        "   - fortalezas y debilidades del equipo,\r\n" + //
                        "   - recomendaciones específicas (estructuras de datos, algoritmos, optimizaciones, patrones de solución),\r\n" + //
                        "   - sugerencias de estudio,\r\n" + //
                        "   - análisis de patrones de fallos.\r\n" + //
                        "\r\n" + //
                        "2. **Da retroalimentación constructiva**, siempre con tono profesional, motivador y basado en evidencia.\r\n" + //
                        "\r\n" + //
                        "3. **Si la información del envío no es suficiente**, solicita los datos faltantes de manera amable.\r\n" + //
                        "\r\n" + //
                        "4. **Si el usuario no está hablando de programación o maratones**, respóndele normalmente como un asistente conversacional. No fuerces retroalimentación competitiva si no corresponde.\r\n" + //
                        "\r\n" + //
                        "5. Sé conciso pero informativo. Evita tecnicismos innecesarios, pero puedes usarlos cuando se justifique.\r\n" + //
                        "\r\n" + //
                        "6. Mantén un estilo claro, organizado y fácil de entender. Usa viñetas o listas cuando ayuden a la comprensión." +
                "\nEquipo: " + envio.getEquipo().getNombre() +
                "\nProblema: " + envio.getProblema().getTitulo() +
                "\nResultado: " + envio.getResultado() +
                "\nTiempo de ejecución: " + envio.getTiempoEjecucion() + " segundos." +
                "\nMemoria utilizada: " + envio.getMemoriaUsada() + " KB." +
                "\n\nPor favor, responde a las siguientes preguntas o proporciona retroalimentación general sobre el desempeño del equipo.";

        try {
            Mono<String> response = openRouterWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue("{" +
                            "\"model\": \"gpt-4o-mini\", " +
                            "\"messages\": [{\"role\": \"system\", \"content\": \"" + prompt + "\"}]" +
                            "}")
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> Mono.error(new RuntimeException("Error del cliente al llamar a la API de OpenRouter.")))
                    .onStatus(status -> status.is5xxServerError(), clientResponse -> Mono.error(new RuntimeException("Error del servidor al llamar a la API de OpenRouter.")))
                    .bodyToMono(String.class);

            String retroalimentacion = response.block();
            if (retroalimentacion != null) {
                // Filtrar líneas que comienzan con '*'
                retroalimentacion = Arrays.stream(retroalimentacion.split("\\n"))
                        .filter(line -> !line.trim().startsWith("*"))
                        .filter(line -> !line.toLowerCase().contains("pensando")) // Filtrar pensamientos
                        .collect(Collectors.joining("\n"));
            }

            Retroalimentacion nuevaRetroalimentacion = Retroalimentacion.builder()
                    .envio(envio)
                    .tipo(TipoRetro.ia)
                    .comentario(retroalimentacion != null ? retroalimentacion : "Retroalimentación no disponible.")
                    .build();

            retroalimentacionRepository.save(nuevaRetroalimentacion);

            return retroalimentacion;
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error al comunicarse con la API de OpenRouter: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error en los datos proporcionados: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al generar la retroalimentación: " + e.getMessage(), e);
        }
    }
}