package pro.dkart.wordflow.blog.infrastructure.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.dkart.wordflow.blog.domain.model.Post;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = {"translations"})
    List<Post> findAll();

    @EntityGraph(attributePaths = {"translations"})
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findLatest(PageRequest pageable);

    Post getByLink(String link);

    boolean existsBySlug(String slug);

    Optional<Post> findBySlug(String slug);
}