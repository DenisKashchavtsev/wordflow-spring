package pro.dkart.wordflow.blog.application.dto.response;

import java.time.LocalDateTime;

public record PostResponse(
        String level, String metaTitle, String metaDescription, String keywords, String title, String content, String slug, String image, LocalDateTime createdAt) {
}
