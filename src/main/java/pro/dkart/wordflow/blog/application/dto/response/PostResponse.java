package pro.dkart.wordflow.blog.application.dto.response;

import java.time.LocalDateTime;

public record PostResponse(
        String level, String metaTitle, String metaDescription, String keywords, String slug, String title, String content, String imageUrl, String link, LocalDateTime createdAt) {
}
