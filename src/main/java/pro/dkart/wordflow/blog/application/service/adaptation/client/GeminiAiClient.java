package pro.dkart.wordflow.blog.application.service.adaptation.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pro.dkart.wordflow.blog.application.dto.AdaptedArticle;
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

    public AdaptedArticle adaptText(String originalText, LanguageRangeLevel level) {
        String prompt = String.format(
                "Simplify the following English text to CEFR level %s.\n" +
                        "Then write a unique new article inspired by the simplified content and ideas. Do not copy structure or wording, don't mention source or author.\n" +
                        "Return the result as HTML with the title wrapped in triple asterisks (***). Include proper HTML tags just <ul>, <li>, <p>, <b>, <a>, <h2>, <h3>, <h4>, <h5>, <h6>.\n" +
                        "Respond **only** with the full HTML content, Do not include any explanations, formatting, introductions, or additional notes:\n\n%s",

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
            String html = firstPart.get("text").toString().trim();

            // Парсим заголовок из ***Title***
            String title = null;
            String body = html;

            int start = html.indexOf("***");
            int end = html.indexOf("***", start + 3);

            if (start != -1 && end != -1 && end > start) {
                title = html.substring(start + 3, end).trim();
                body = html.substring(end + 3).trim();
            }

            return new AdaptedArticle(title, body);

        } catch (Exception e) {
            log.error("Ошибка при обращении к Gemini AI: {}", e.getMessage(), e);
            return new AdaptedArticle(null, originalText); // fallback
        }
    }
}