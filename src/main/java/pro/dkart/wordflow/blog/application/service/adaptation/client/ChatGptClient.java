package pro.dkart.wordflow.blog.application.service.adaptation.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pro.dkart.wordflow.kernel.LanguageLevel;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatGptClient {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public String adaptText(String originalText, LanguageLevel level) {
        String prompt = "Адаптируй следующий текст под уровень английского " + level.name() + ":\n\n" + originalText;

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",  // вот здесь поменяли модель
                "messages", List.of(
                        Map.of("role", "system", "content", "Ты помощник по упрощению английского текста."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7
        );

        try {
            System.out.println("request");

            Map response = webClient.post()
                    .header("Authorization", "Bearer " + apiKey)
                    .body(Mono.just(requestBody), Map.class)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            List choices = (List) response.get("choices");
            Map choice = (Map) choices.get(0);
            Map message = (Map) choice.get("message");
            return message.get("content").toString().trim();

        } catch (Exception e) {
            log.error("Ошибка при обращении к ChatGPT: {}", e.getMessage(), e);
            return originalText; // fallback
        }
    }
}
