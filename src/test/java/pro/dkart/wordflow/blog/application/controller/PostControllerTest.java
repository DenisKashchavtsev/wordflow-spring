package pro.dkart.wordflow.blog.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pro.dkart.wordflow.blog.domain.model.Post;
import pro.dkart.wordflow.blog.infrastructure.repository.PostRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    private Post samplePost;

    @BeforeEach
    void setup() {
        postRepository.deleteAll();

        samplePost = new Post();
        samplePost.setCreatedAt(LocalDateTime.now());
        postRepository.save(samplePost);
        samplePost = postRepository.findById(samplePost.getId()).orElseThrow();
    }

    @Test
    void testListPosts() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postRepository.findAll())));
    }

    @Test
    void testGetPost() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/posts/{id}", samplePost.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Post responsePost = objectMapper.readValue(jsonResponse, Post.class);

        assertEquals(samplePost.getId(), responsePost.getId());

        long diff = Math.abs(samplePost.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() -
                responsePost.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        assertTrue(diff < 5, "createdAt timestamps differ more than 5 ms");
    }
}
