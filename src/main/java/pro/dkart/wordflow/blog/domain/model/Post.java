package pro.dkart.wordflow.blog.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Post {
    @Id
    private Long id;

    private String title;
    private String content;
    private Long authorId;

    private LocalDateTime createdAt;
}
