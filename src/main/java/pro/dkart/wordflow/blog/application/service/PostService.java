package pro.dkart.wordflow.blog.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pro.dkart.wordflow.blog.application.dto.response.PostResponse;
import pro.dkart.wordflow.blog.application.dto.response.PostsResponse;
import pro.dkart.wordflow.blog.domain.model.Post;
import pro.dkart.wordflow.blog.domain.model.PostTranslation;
import pro.dkart.wordflow.blog.infrastructure.repository.PostRepository;
import pro.dkart.wordflow.kernel.LanguageRangeLevel;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public PostsResponse findAllByLevel(String level, Integer page) {
        int pageNumber = page != null && page > 0 ? page - 1 : 0;
        int pageSize = 10; // можешь вынести в константу или параметр

        LanguageRangeLevel rangeLevel = null;
        try {
            if (level != null) {
                rangeLevel = LanguageRangeLevel.valueOf(level.toUpperCase(Locale.ROOT));
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid level: " + level);
        }

        var pageable = PageRequest.of(pageNumber, pageSize);
        var pageResult = postRepository.findLatest(pageable); // твой кастомный метод

        LanguageRangeLevel finalRangeLevel = rangeLevel;
        List<PostResponse> posts = pageResult.stream()
                .map(post -> {
                    boolean isOriginal = finalRangeLevel == null || finalRangeLevel == LanguageRangeLevel.C1_C2;
                    String title = isOriginal ? post.getTitle()
                            : Optional.ofNullable(post.getTranslations().get(finalRangeLevel))
                            .map(PostTranslation::getTitle).orElse(post.getTitle());

                    String content = isOriginal ? post.getContent()
                            : Optional.ofNullable(post.getTranslations().get(finalRangeLevel))
                            .map(PostTranslation::getContent).orElse(post.getContent());

                    return new PostResponse(
                            isOriginal ? "C1_C2" : finalRangeLevel.name(),
                            post.getMetaTitle(),
                            post.getMetaDescription(),
                            post.getKeywords(),
                            post.getSlug(),
                            title,
                            content,
                            post.getImageUrl(),
                            post.getLink(),
                            post.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());

        return new PostsResponse(
                pageNumber + 1,
                pageResult.size(),
                posts
        );
    }

    public List<Post> getLatest(Long limit) {
        return postRepository.findLatest(PageRequest.of(0, limit.intValue()));
    }

    public PostResponse findByIdAndLevel(String slug, String level) {

        Post post = postRepository.findBySlug(slug).orElseThrow();

        PostTranslation translation = post.getTranslations().get(LanguageRangeLevel.valueOf(level));

        if (!Objects.equals(level, LanguageRangeLevel.C1_C2.name())) {
            return new PostResponse(
                    level,
                    post.getMetaTitle(),
                    post.getMetaDescription(),
                    post.getKeywords(),
                    post.getSlug(),
                    translation.getTitle(),
                    translation.getContent(),
                    post.getImageUrl(),
                    post.getLink(),
                    post.getCreatedAt()
            );
        }

        return new PostResponse(
                level,
                post.getMetaTitle(),
                post.getMetaDescription(),
                post.getKeywords(),
                post.getSlug(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                post.getLink(),
                post.getCreatedAt()
        );
    }
}