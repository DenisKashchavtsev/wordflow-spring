package pro.dkart.wordflow.blog.application.service.adaptation;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.dkart.wordflow.blog.application.service.adaptation.client.ChatGptClient;
import pro.dkart.wordflow.blog.application.service.adaptation.client.GeminiAiClient;
import pro.dkart.wordflow.blog.application.service.adaptation.parser.GoodNewsNetworkParser;
import pro.dkart.wordflow.blog.domain.model.Post;
import pro.dkart.wordflow.blog.domain.model.PostTranslation;
import pro.dkart.wordflow.blog.infrastructure.repository.PostRepository;
import pro.dkart.wordflow.kernel.LanguageRangeLevel;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsAdaptationService {

    private final PostRepository postRepository;
    private final ChatGptClient chatGptClient;
    private final GeminiAiClient geminiAiClient;
    private final GoodNewsNetworkParser goodNewsNetworkParser;

    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void fetchAndAdaptNews() throws Exception {
        log.info("Запуск адаптации новостей...");

        goodNewsNetworkParser.parse();
    }

}