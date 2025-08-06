package pro.dkart.wordflow.blog.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.dkart.wordflow.blog.application.dto.response.PostResponse;
import pro.dkart.wordflow.blog.application.dto.response.PostsResponse;
import pro.dkart.wordflow.blog.application.service.PostService;
import pro.dkart.wordflow.blog.domain.model.Post;
import pro.dkart.wordflow.kernel.LanguageRangeLevel;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public PostsResponse listPosts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Integer limit) {
        level = level == null ? LanguageRangeLevel.C1_C2.name() : level;
        limit = limit == null || limit > 25 ? 10 : limit;

        return postService.findAllByLevel(level, page, limit);
    }

    @GetMapping("/{slug}")
    public PostResponse getPost(@PathVariable String slug, @RequestParam(value = "level", required = false) String level) {
        level = level == null ? LanguageRangeLevel.C1_C2.name() : level;

        return postService.findByIdAndLevel(slug, level);
    }
}