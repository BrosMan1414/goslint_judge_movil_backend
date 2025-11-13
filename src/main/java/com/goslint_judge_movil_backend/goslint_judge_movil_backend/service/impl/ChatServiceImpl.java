package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.impl;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.ai.ChatMessage;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.ai.ChatRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.ai.ChatResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.chat.ChatTurnResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.exception.NotFoundException;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.ChatTurn;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Equipo;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Maraton;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.ChatTurnRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EquipoRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.MaratonRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EnvioRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatTurnRepository chatTurnRepository;
    private final EquipoRepository equipoRepository;
    private final MaratonRepository maratonRepository;
    private final EnvioRepository envioRepository;
    private final WebClient openRouterWebClient;

    @Value("${openrouter.api.key:}")
    private String apiKey;

    public ChatServiceImpl(ChatTurnRepository chatTurnRepository,
                           EquipoRepository equipoRepository,
                           MaratonRepository maratonRepository,
                           EnvioRepository envioRepository,
                           WebClient openRouterWebClient) {
        this.chatTurnRepository = chatTurnRepository;
        this.equipoRepository = equipoRepository;
        this.maratonRepository = maratonRepository;
        this.envioRepository = envioRepository;
        this.openRouterWebClient = openRouterWebClient;
    }

    @Override
    public ChatTurnResponse enviarMensaje(Long equipoId, Long maratonId, String mensajeUsuario) {
        Equipo equipo = equipoRepository.findById(equipoId).orElseThrow(() -> new NotFoundException("Equipo no encontrado"));
        Maraton maraton = maratonRepository.findById(maratonId).orElseThrow(() -> new NotFoundException("Maratón no encontrada"));

        List<ChatTurn> history = chatTurnRepository.findByEquipo_IdAndMaraton_IdOrderByCreatedAtAsc(equipoId, maratonId);
        int totalEnvios = envioRepository.findByEquipo_IdAndProblema_Maraton_Id(equipoId, maratonId).size();
        long aceptados = envioRepository.findByEquipo_IdAndProblema_Maraton_Id(equipoId, maratonId)
                .stream().filter(e -> e.getResultado() != null && e.getResultado().name().equals("Accepted")).count();

        double porcentaje = totalEnvios == 0 ? 0.0 : (aceptados * 100.0 / totalEnvios);

        String context = "Equipo: " + equipo.getNombre() + " | Maratón: " + maraton.getNombre() +
                " | Envíos totales: " + totalEnvios + " | Aceptados: " + aceptados + " (" + String.format("%.2f", porcentaje) + "%)";

        String historySummary = history.stream()
                .skip(Math.max(0, history.size() - 5))
                .map(h -> "Usuario: " + h.getUserMessage() + "\nIA: " + h.getAiResponse())
                .collect(Collectors.joining("\n---\n"));

        String systemPrompt = "Eres un coach experto en programación competitiva para un equipo hispanohablante. " +
                "Responde SIEMPRE en español con viñetas accionables. Formato:\n" +
                "- Resumen breve de la situación (1 línea).\n" +
                "- Sugerencias (entre 2 y 5 bullets, comenzando con un verbo en infinitivo).\n" +
                "- Próximo paso recomendado (1 línea). No repitas texto previo.";

        String userPrompt = "Contexto actual:\n" + context + "\nÚltimos turnos:\n" + (historySummary.isBlank() ? "(sin historial)" : historySummary) +
                "\nMensaje del usuario:\n" + mensajeUsuario;

        // Fallback API key env
        if (apiKey == null || apiKey.isBlank()) {
            String envKey = System.getenv("OPENROUTER_API_KEY");
            if (envKey != null && !envKey.isBlank()) apiKey = envKey;
        }

        String aiResponse;
        if (apiKey == null || apiKey.isBlank()) {
            aiResponse = "[Demo] No hay clave OpenRouter. Ejemplo de respuesta:\n- Resumen: estás consultando progreso.\n- Sugerencias:\n  * Revisar envíos aceptados para detectar patrones.\n  * Enfocar próximos intentos en problemas fallidos de menor dificultad.\n- Próximo paso: elegir un problema fallido y reintentar optimizando complejidad.";
        } else {
            ChatRequest request = new ChatRequest(
                    "openai/gpt-4o-mini",
                    java.util.List.of(
                            new ChatMessage("system", systemPrompt),
                            new ChatMessage("user", userPrompt)
                    ),
                    0.3
            );

            Mono<ChatResponse> responseMono = openRouterWebClient.post()
                    .uri("/chat/completions")
                    .headers(h -> h.setBearerAuth(Objects.requireNonNull(apiKey)))
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class);
            ChatResponse chatResponse = responseMono.block();
            aiResponse = (chatResponse != null && chatResponse.getChoices() != null && !chatResponse.getChoices().isEmpty() && chatResponse.getChoices().get(0).getMessage() != null)
                    ? chatResponse.getChoices().get(0).getMessage().getContent() : "Respuesta IA no disponible";
        }

        ChatTurn turn = ChatTurn.builder()
                .equipo(equipo)
                .maraton(maraton)
                .userMessage(mensajeUsuario)
                .aiResponse(aiResponse)
                .build();
        chatTurnRepository.save(turn);

        return ChatTurnResponse.builder()
                .id(turn.getId())
                .mensajeUsuario(turn.getUserMessage())
                .respuestaIA(turn.getAiResponse())
                .fecha(turn.getCreatedAt())
                .build();
    }

    @Override
    public List<ChatTurnResponse> historial(Long equipoId, Long maratonId) {
        return chatTurnRepository.findByEquipo_IdAndMaraton_IdOrderByCreatedAtAsc(equipoId, maratonId)
                .stream().map(t -> ChatTurnResponse.builder()
                        .id(t.getId())
                        .mensajeUsuario(t.getUserMessage())
                        .respuestaIA(t.getAiResponse())
                        .fecha(t.getCreatedAt())
                        .build()).collect(Collectors.toList());
    }
}
