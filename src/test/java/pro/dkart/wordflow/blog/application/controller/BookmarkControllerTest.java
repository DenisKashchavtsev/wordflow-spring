package pro.dkart.wordflow.blog.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pro.dkart.wordflow.blog.domain.model.Bookmark;
import pro.dkart.wordflow.blog.domain.model.Post;
import pro.dkart.wordflow.blog.infrastructure.repository.BookmarkRepository;
import pro.dkart.wordflow.blog.infrastructure.repository.PostRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    private Post samplePost;

    @BeforeEach
    void setup() {
        bookmarkRepository.deleteAll();
        postRepository.deleteAll();

        samplePost = new Post();
        samplePost.setCreatedAt(LocalDateTime.now());
        postRepository.save(samplePost);
        samplePost = postRepository.findById(samplePost.getId()).orElseThrow();
    }

    @Test
    void testAddBookmark() throws Exception {
        mockMvc.perform(post("/api/bookmarks/{postId}", samplePost.getId())
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk());
    }

    @Transactional
    @Test
    void testRemoveBookmark() throws Exception {
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(10L);
        bookmark.setPost(samplePost);
        bookmarkRepository.save(bookmark);

        mockMvc.perform(delete("/api/bookmarks/{postId}", samplePost.getId())
                        .header("X-User-Id", "10"))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @Test
    void testListBookmarks() throws Exception {
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(10L);
        bookmark.setPost(samplePost);
        bookmarkRepository.save(bookmark);

        mockMvc.perform(get("/api/bookmarks")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookmarkRepository.findByUserId(10L))));
    }
}
