package com.goslint_judge_movil_backend.goslint_judge_movil_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class OpenRouterConfig {

    @Value("${openrouter.api.key:}")
    private String apiKey;

    private static final Logger log = LoggerFactory.getLogger(OpenRouterConfig.class);

    @Bean
    public WebClient openRouterWebClient() {
        // Fallback: si no vino por properties, intenta leer de la variable de entorno del proceso
        String key = (apiKey != null && !apiKey.isBlank()) ? apiKey : System.getenv("OPENROUTER_API_KEY");

        WebClient.Builder builder = WebClient.builder()
                .baseUrl("https://openrouter.ai/api/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        if (key != null && !key.isBlank()) {
            builder = builder.defaultHeader("Authorization", "Bearer " + key);
            String tail = key.length() > 6 ? key.substring(key.length() - 6) : "******";
            log.info("OpenRouter: API key detectada (…{}).", tail);
        } else {
            log.warn("OpenRouter: sin API key, el servicio usará modo demo.");
        }
        return builder.build();
    }
}
