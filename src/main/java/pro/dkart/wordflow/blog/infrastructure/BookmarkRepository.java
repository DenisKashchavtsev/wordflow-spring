package pro.dkart.wordflow.blog.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.dkart.wordflow.blog.domain.model.Bookmark;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByUserId(Long userId);

    Optional<Bookmark> findByUserIdAndPost_Id(Long userId, Long postId);

    boolean existsByUserIdAndPost_Id(Long userId, Long postId);

    void deleteByUserIdAndPost_Id(Long userId, Long postId);
}