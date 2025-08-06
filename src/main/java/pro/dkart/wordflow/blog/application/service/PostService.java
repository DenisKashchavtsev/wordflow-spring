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

    public PostsResponse findAllByLevel(String level, Integer page, Integer limit) {
        int pageNumber = page != null && page > 0 ? page - 1 : 0;
        int pageSize = limit != null ? limit : 10;

        LanguageRangeLevel rangeLevel = null;
        if (level != null) {
            rangeLevel = LanguageRangeLevel.valueOf(level.toUpperCase(Locale.ROOT));
        } else {
            rangeLevel = LanguageRangeLevel.valueOf("B1_B2");
        }

        var pageable = PageRequest.of(pageNumber, pageSize);
        var pageResult = postRepository.findLatest(pageable); // твой кастомный метод

        LanguageRangeLevel finalRangeLevel = rangeLevel;
        List<PostResponse> posts = pageResult.stream()
                .map(post -> {

                    PostTranslation postTranslation = post.getTranslations().get(finalRangeLevel);

                    return new PostResponse(
                            finalRangeLevel.name(),
                            postTranslation.getMetaTitle(),
                            postTranslation.getMetaDescription(),
                            postTranslation.getKeywords(),
                            postTranslation.getTitle(),
                            postTranslation.getContent(),
                            post.getSlug(),
                            post.getImage(),
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

//    public List<Post> getLatest(Long limit) {
//        return postRepository.findLatest(PageRequest.of(0, limit.intValue()));
//    }

    public PostResponse findByIdAndLevel(String slug, String level) {

        Post post = postRepository.findBySlug(slug).orElseThrow();

        PostTranslation postTranslation = post.getTranslations().get(LanguageRangeLevel.valueOf(level));

        return new PostResponse(
                level,
                postTranslation.getMetaTitle(),
                postTranslation.getMetaDescription(),
                postTranslation.getKeywords(),
                postTranslation.getTitle(),
                postTranslation.getContent(),
                post.getSlug(),
                post.getImage(),
                post.getCreatedAt()
        );
    }
}