package pro.dkart.wordflow.blog.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import pro.dkart.wordflow.kernel.LanguageLevel;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

@Entity
@Data
public class Post {
    @Id
    private Long id;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @MapKey(name = "level")
    private Map<LanguageLevel, PostTranslation> translations = new EnumMap<>(LanguageLevel.class);

    private LocalDateTime createdAt = LocalDateTime.now();
}
