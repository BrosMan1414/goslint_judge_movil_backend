package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.ai.ChatMessage;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.ai.ChatRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.ai.ChatResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Retroalimentacion;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Envio;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.TipoRetro;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EnvioRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.RetroalimentacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.springframework.lang.NonNull;
import java.util.Objects;

@Service
public class RetroalimentacionIAService {

    @Autowired
    private WebClient openRouterWebClient;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private RetroalimentacionRepository retroalimentacionRepository;
    
    @Value("${openrouter.api.key:}")
    private String apiKey;

    public String generarRetroalimentacionIA(@NonNull Long envioId) {
        Envio envio = envioRepository.findById(envioId).orElseThrow(() -> new IllegalArgumentException("El envío no existe."));

        String prompt = "Eres un asistente experto en programación competitiva. Aquí está el contexto: " +
                "\nEquipo: " + envio.getEquipo().getNombre() +
                "\nProblema: " + envio.getProblema().getTitulo() +
                "\nResultado: " + envio.getResultado() +
                "\nTiempo: " + envio.getTiempoEjecucion() + "\n";

    try {
        // Fallback: si no vino por properties, intenta leer del entorno del proceso
        if (apiKey == null || apiKey.isBlank()) {
        String envKey = System.getenv("OPENROUTER_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            apiKey = envKey;
        }
        }

        // Si aún no hay clave => modo demo
        if (apiKey == null || apiKey.isBlank()) {
        String retro = "[Modo demo sin OpenRouter] Retroalimentación para el equipo '" + envio.getEquipo().getNombre() +
            "' sobre el problema '" + envio.getProblema().getTitulo() + "'. Resultado: " + envio.getResultado() + ".";

        Retroalimentacion r = Retroalimentacion.builder()
            .envio(envio)
            .tipo(TipoRetro.ia)
            .comentario(retro)
            .build();
    retroalimentacionRepository.save(Objects.requireNonNull(r));
        return retro;
        }
        ChatRequest request = new ChatRequest(
            "openai/gpt-4o-mini",
            java.util.List.of(
                new ChatMessage("system", "Eres un asistente experto en programación competitiva. Devuelve retroalimentación clara, breve y accionable."),
                new ChatMessage("user", prompt)
            ),
            0.2
        );

        Mono<ChatResponse> response = openRouterWebClient.post()
            .uri("/chat/completions")
            .headers(h -> h.setBearerAuth(Objects.requireNonNull(apiKey)))
            .bodyValue(request)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), clientResponse -> Mono.error(new RuntimeException("Error del cliente al llamar a la API de OpenRouter.")))
            .onStatus(status -> status.is5xxServerError(), clientResponse -> Mono.error(new RuntimeException("Error del servidor al llamar a la API de OpenRouter.")))
            .bodyToMono(ChatResponse.class);

        ChatResponse chat = response.block();
        String retroalimentacion = (chat != null && chat.getChoices() != null && !chat.getChoices().isEmpty() &&
            chat.getChoices().get(0).getMessage() != null) ? chat.getChoices().get(0).getMessage().getContent() : null;

            Retroalimentacion nuevaRetroalimentacion = Retroalimentacion.builder()
                    .envio(envio)
                    .tipo(TipoRetro.ia)
                    .comentario(retroalimentacion != null ? retroalimentacion : "Retroalimentación no disponible.")
                    .build();

            retroalimentacionRepository.save(Objects.requireNonNull(nuevaRetroalimentacion));

            return retroalimentacion;
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error al comunicarse con la API de OpenRouter: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error en los datos proporcionados: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al generar la retroalimentación: " + e.getMessage(), e);
        }
    }

    public String obtenerUltimaRetroIA(@NonNull Long envioId) {
        return retroalimentacionRepository
                .findTopByEnvio_IdAndTipoOrderByFechaDesc(envioId, com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.TipoRetro.ia)
                .map(Retroalimentacion::getComentario)
                .orElseThrow(() -> new com.goslint_judge_movil_backend.goslint_judge_movil_backend.exception.NotFoundException(
                        "No hay retroalimentación IA para el envío " + envioId));
    }
}
