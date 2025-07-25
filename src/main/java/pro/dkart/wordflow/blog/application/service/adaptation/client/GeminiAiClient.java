package pro.dkart.wordflow.blog.application.service.adaptation.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pro.dkart.wordflow.kernel.LanguageLevel;
import pro.dkart.wordflow.kernel.LanguageRangeLevel;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiAiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public String adaptText(String originalText, LanguageRangeLevel level) {
        String prompt = String.format(
                "Simplify the following English text to the %s level. " +
                        "Respond **only** with the simplified version of the text. " +
                        "Do not include any explanations, formatting, introductions, or additional notes:\n\n%s",
                level.name(), originalText
        );
        
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            Map response = webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .body(Mono.just(requestBody), Map.class)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List candidates = (List) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("Empty response from Gemini");
            }

            Map candidate = (Map) candidates.get(0);
            Map content = (Map) candidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);
            return firstPart.get("text").toString().trim();

        } catch (Exception e) {
            log.error("Ошибка при обращении к Gemini AI: {}", e.getMessage(), e);
            return originalText; // fallback
        }
    }
}