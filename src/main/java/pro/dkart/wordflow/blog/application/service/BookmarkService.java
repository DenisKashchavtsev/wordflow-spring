package pro.dkart.wordflow.blog.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.dkart.wordflow.blog.domain.model.Bookmark;
import pro.dkart.wordflow.blog.domain.model.Post;
import pro.dkart.wordflow.blog.infrastructure.BookmarkRepository;
import pro.dkart.wordflow.blog.infrastructure.PostRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;

    public void addBookmark(Long userId, Long postId) {
        if (bookmarkRepository.existsByUserIdAndPost_Id(userId, postId)) {
            return; // Уже есть закладка — ничего не делаем
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setPost(post);
        bookmark.setCreatedAt(LocalDateTime.now());

        bookmarkRepository.save(bookmark);
    }

    public void removeBookmark(Long userId, Long postId) {
        bookmarkRepository.deleteByUserIdAndPost_Id(userId, postId);
    }

    public List<Bookmark> getBookmarks(Long userId) {
        return bookmarkRepository.findByUserId(userId);
    }
}