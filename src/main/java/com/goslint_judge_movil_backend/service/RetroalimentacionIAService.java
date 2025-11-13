package com.goslint_judge_movil_backend.service;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Retroalimentacion;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Envio;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.TipoRetro;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EnvioRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.RetroalimentacionRepository;
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

        String prompt = "Eres un asistente experto en programación competitiva. Aquí está el contexto: " +
                "\nEquipo: " + envio.getEquipo().getNombre() +
                "\nProblema: " + envio.getProblema().getTitulo() +
                "\nResultado: " + envio.getResultado() +
                "\nTiempo: " + envio.getTiempoEjecucion() + "\n";

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