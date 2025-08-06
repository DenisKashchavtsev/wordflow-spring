package pro.dkart.wordflow.blog.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import pro.dkart.wordflow.kernel.LanguageRangeLevel;

import java.time.LocalDateTime;

@Entity
@Data
public class PostTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 5)
    @Enumerated(EnumType.STRING)
    private LanguageRangeLevel level;

    private String metaTitle;

    @Column(length = 500)
    private String metaDescription;

    @Column(length = 500)
    private String keywords;

    private String title;

    @Column(length = 50000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;
}