package pro.dkart.wordflow.blog.application.service.adaptation.parser;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import pro.dkart.wordflow.blog.application.dto.AdaptedArticle;
import pro.dkart.wordflow.blog.application.service.adaptation.ImageService;
import pro.dkart.wordflow.blog.application.service.adaptation.client.GeminiAiClient;
import pro.dkart.wordflow.blog.domain.model.Post;
import pro.dkart.wordflow.blog.domain.model.PostTranslation;
import pro.dkart.wordflow.blog.infrastructure.repository.PostRepository;
import pro.dkart.wordflow.kernel.LanguageRangeLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;

@Component
@Slf4j
public class GoodNewsNetworkParser extends Parser {

    private final GeminiAiClient geminiAiClient;
    public static String rss = "https://www.goodnewsnetwork.org/feed/";

    public GoodNewsNetworkParser(PostRepository postRepository, GeminiAiClient geminiAiClient) {
        super(postRepository);
        this.geminiAiClient = geminiAiClient;
    }

    public void parse() throws Exception {
        SyndFeed feed = loadRss(rss);
        try {
            for (SyndEntry entry : feed.getEntries()) {
                if (entry.getPublishedDate().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                        .isEqual(LocalDate.now().minusDays(1))) {

                    String articleUrl = entry.getLink();
                    String originalText = extractArticleText(articleUrl);
                    String originalTitle = entry.getTitle(); // Get original title
                    if (originalText.isBlank()) continue;

                    Post post = postRepository.getByLink(entry.getLink());
                    if (post == null) {
                        System.out.println("Сохранение статьи: {}" + originalTitle);
                        post = new Post();
                        post.setSlug(generateUniqueSlug(originalTitle));
                        String originalImageUrl = extractArticleImage(articleUrl);
                        if (!originalImageUrl.isEmpty()) {
                            String localWebpUrl = ImageService.downloadAndConvertImageToWebP(originalImageUrl);
                            post.setImage(localWebpUrl);
                        }
                        post.setLink(articleUrl);
                        post.setCreatedAt(LocalDateTime.now());

                        EnumMap<LanguageRangeLevel, PostTranslation> translations = new EnumMap<>(LanguageRangeLevel.class);
                        for (LanguageRangeLevel level : LanguageRangeLevel.values()) {
                            PostTranslation translation = post.getTranslations().get(level);
                            if (translation == null) {
                                // Include original title in the content sent to AI
                                String contentWithTitle = "***" + originalTitle + "***\n\n" + originalText;
                                AdaptedArticle adapted = geminiAiClient.adaptText(contentWithTitle, level);

                                translation = new PostTranslation();
                                translation.setLevel(level);
                                translation.setTitle(adapted.title()); // Use adapted title from AI
                                translation.setMetaTitle(adapted.title() + " | WordFlow");
                                translation.setMetaDescription(generateDescription(adapted.title(), adapted.htmlContent()));
                                translation.setKeywords(generateKeywords(adapted.title()));
                                translation.setContent(adapted.htmlContent());
                                translation.setPost(post);
                                translations.put(level, translation);

                                Thread.sleep(40);
                            }
                        }

                        post.setTranslations(translations);
                        postRepository.save(post);
                        System.out.println("Сохранена статья: {}" + originalTitle);
                        break;
                    } else {
                        System.out.println("Статья уже есть: {}" + originalTitle);
                    }

                    Thread.sleep(40);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при адаптации новостей", e);
        }
    }

    public String extractArticleText(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Element content = doc.selectFirst("div.td-post-content");

            if (content == null) {
                log.warn("Контент не найден по селектору td-post-content на {}", url);
                return "";
            }

            content.select("script, .sharedaddy, .td_block_wrap").remove();

            return content.text().trim();
        } catch (Exception e) {
            log.error("Ошибка при парсинге текста на {}: {}", url, e.getMessage(), e);
            return "";
        }
    }

    public String extractArticleImage(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Element image = doc.selectFirst("div.td-post-content figure img");

            if (image == null) {
                log.warn("Изображение не найдено по селектору figure img на {}", url);
                return "";
            }

            return image.attr("src").trim();
        } catch (Exception e) {
            log.error("Ошибка при парсинге изображения на {}: {}", url, e.getMessage(), e);
            return "";
        }
    }
}