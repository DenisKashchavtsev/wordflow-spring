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
import pro.dkart.wordflow.kernel.LanguageLevel;
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
    public void fetchAndAdaptNews() {
        log.info("Запуск адаптации новостей...");

        try {
            SyndFeed feed = loadRss("https://www.goodnewsnetwork.org/feed/");
            for (SyndEntry entry : feed.getEntries()) {
                System.out.println(entry.getPublishedDate().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                        .isEqual(LocalDate.now()));
                if (entry.getPublishedDate().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                        .isEqual(LocalDate.now().minusDays(1))) {

                    String articleUrl = entry.getLink();
                    String originalText = goodNewsNetworkParser.extractArticleText(articleUrl);
                    if (originalText.isBlank()) continue;

                    Post post = postRepository.getByLink(entry.getLink());
                    if (post == null) {

                        post = new Post();
                        post.setLink(entry.getLink());
                        post.setTitle(entry.getTitle());
                        post.setContent(originalText);
                        post.setMetaTitle(entry.getTitle() + " | WordFlow");

                        String shortContent = originalText.length() > 300 ? originalText.substring(0, 300).replaceAll("\\s+", " ").trim() + "..." : originalText;
                        post.setMetaDescription("Learn English through good news: \"" + entry.getTitle() + "\" — simplified for language learners. " + shortContent);
                        post.setKeywords(generateKeywords(entry.getTitle()));

                        post.setImageUrl(goodNewsNetworkParser.extractArticleImage(articleUrl));

                        post.setCreatedAt(LocalDateTime.now());

                        EnumMap<LanguageRangeLevel, PostTranslation> translations = new EnumMap<>(LanguageRangeLevel.class);
                        for (LanguageRangeLevel level : LanguageRangeLevel.values()) {
                            if (level == LanguageRangeLevel.A1_A2 || level == LanguageRangeLevel.B1_B2) {
                                PostTranslation translation = post.getTranslations().get(level);
                                if (translation == null) {
                                    String adapted = geminiAiClient.adaptText(originalText, level);
                                    translation = new PostTranslation();
                                    translation.setLevel(level);
                                    translation.setTitle(post.getTitle());
                                    translation.setContent(adapted);
                                    translation.setPost(post);
                                    translations.put(level, translation);

                                    Thread.sleep(40);
                                }
                            }
                        }

                        post.setTranslations(translations);
                        postRepository.save(post);
                        System.out.println("Сохранена статья: {}" + entry.getTitle());
                    } else {
                        System.out.println("Статья уже есть: {}" + entry.getTitle());
                    }

                    Thread.sleep(40);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при адаптации новостей", e);
        }
    }

    private SyndFeed loadRss(String url) throws Exception {
        URI uri = URI.create(url);
        try (InputStream is = uri.toURL().openStream()) {
            XmlReader reader = new XmlReader(is);
            return new SyndFeedInput().build(reader);
        }
    }

    private String generateKeywords(String title) {
        // Примитивная разбивка по словам, можно доработать фильтрами стоп-слов и т.п.
        return title.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "") // убираем знаки препинания
                .replaceAll("\\s+", ", "); // заменяем пробелы на запятые
    }

}