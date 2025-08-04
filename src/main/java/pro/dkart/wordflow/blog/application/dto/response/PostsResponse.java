package pro.dkart.wordflow.blog.application.dto.response;

import java.util.List;

public record PostsResponse(
        Integer totalPages, Integer total, List<PostResponse> posts) {
}
