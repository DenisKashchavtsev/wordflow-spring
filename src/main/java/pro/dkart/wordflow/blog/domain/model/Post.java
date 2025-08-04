package pro.dkart.wordflow.blog.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import pro.dkart.wordflow.kernel.LanguageLevel;
import pro.dkart.wordflow.kernel.LanguageRangeLevel;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @OneToMany(mappedBy = "post",  cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @MapKey(name = "level")
    private Map<LanguageRangeLevel, PostTranslation> translations = new EnumMap<>(LanguageRangeLevel.class);

    private String link;

    private String imageUrl;

    private String metaTitle;

    @Column(length = 500)
    private String metaDescription;

    @Column(length = 500)
    private String keywords;

    private String title;

    @Column(length = 50000)
    private String content;

    @Column(unique = true, nullable = false)
    private String slug;

    private LocalDateTime createdAt = LocalDateTime.now();
}
