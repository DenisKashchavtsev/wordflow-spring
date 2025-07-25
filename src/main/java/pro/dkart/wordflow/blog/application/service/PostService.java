package pro.dkart.wordflow.blog.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.dkart.wordflow.blog.domain.model.Post;
import pro.dkart.wordflow.blog.infrastructure.PostRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }
}