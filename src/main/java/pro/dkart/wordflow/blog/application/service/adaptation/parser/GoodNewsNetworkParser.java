package pro.dkart.wordflow.blog.application.service.adaptation.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GoodNewsNetworkParser {

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
