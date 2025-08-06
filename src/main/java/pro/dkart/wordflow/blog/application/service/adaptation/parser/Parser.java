package pro.dkart.wordflow.blog.application.service.adaptation.parser;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pro.dkart.wordflow.blog.infrastructure.repository.PostRepository;

import java.io.InputStream;
import java.net.URI;

@Component
@RequiredArgsConstructor
public abstract class Parser {

    protected final PostRepository postRepository;

    protected SyndFeed loadRss(String url) throws Exception {
        URI uri = URI.create(url);
        try (InputStream is = uri.toURL().openStream()) {
            XmlReader reader = new XmlReader(is);
            return new SyndFeedInput().build(reader);
        }
    }

    protected String generateDescription(String title, String description) {       
        String shortContent = description.length() > 300 ? description.substring(0, 300).replaceAll("\\s+", " ").trim() + "..." : description;
        
        return "Read and learn English with this good news story: \"" + title + "\" — simplified version. " + shortContent;
    }

    protected String generateKeywords(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "") // убираем знаки препинания
                .replaceAll("\\s+", ", "); // заменяем пробелы на запятые
    }

    protected String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("[-]+", "-")
                .replaceAll("^-|-$", "");
    }

    protected String generateUniqueSlug(String title) {
        String baseSlug = generateSlug(title);
        String slug = baseSlug;
        int index = 1;

        while (postRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + index++;
        }

        return slug;
    }
}
