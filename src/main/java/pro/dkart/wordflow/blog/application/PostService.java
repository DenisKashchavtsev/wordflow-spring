package pro.dkart.wordflow.blog.application;

import org.springframework.stereotype.Service;
import pro.dkart.wordflow.blog.domain.model.Post;
import pro.dkart.wordflow.blog.infrastructure.PostRepository;

import java.util.List;

@Service
public class PostService {

    private final PostRepository repo;

    public PostService(PostRepository repo) {
        this.repo = repo;
    }

    public List<Post> findAll() {
        return repo.findAll();
    }
}