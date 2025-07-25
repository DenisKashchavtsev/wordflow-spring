package pro.dkart.wordflow.blog.infrastructure.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.dkart.wordflow.blog.domain.model.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = {"translations"})
    List<Post> findAll();

    Post getByLink(String link);
}