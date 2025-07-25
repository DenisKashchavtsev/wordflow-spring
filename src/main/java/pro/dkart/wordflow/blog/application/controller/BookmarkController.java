package pro.dkart.wordflow.blog.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.dkart.wordflow.blog.application.service.BookmarkService;
import pro.dkart.wordflow.blog.domain.model.Bookmark;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{postId}")
    public ResponseEntity<Void> addBookmark(@RequestHeader("X-User-Id") Long userId,
                                            @PathVariable Long postId) {
        bookmarkService.addBookmark(userId, postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> removeBookmark(@RequestHeader("X-User-Id") Long userId,
                                               @PathVariable Long postId) {
        bookmarkService.removeBookmark(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Bookmark>> listBookmarks(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(bookmarkService.getBookmarks(userId));
    }
}