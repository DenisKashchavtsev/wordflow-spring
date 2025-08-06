package pro.dkart.wordflow.blog.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.dkart.wordflow.blog.application.service.adaptation.NewsAdaptationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AdaptationTextTest {

    @Autowired
    NewsAdaptationService fetchNewsCommand;

    @Test
    void testNewsFetching() throws Exception {
        fetchNewsCommand.fetchAndAdaptNews();
        assertTrue(true);
    }
}