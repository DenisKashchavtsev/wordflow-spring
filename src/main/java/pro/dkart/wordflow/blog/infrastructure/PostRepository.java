package pro.dkart.wordflow.blog.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.dkart.wordflow.blog.domain.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {}