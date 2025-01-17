package org.site.forum.domain.topic.dao;

import org.junit.jupiter.api.Test;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TopicDaoImpl.class)
public class TopicDaoTests {

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveTopic() {
        var author = User.builder()
                .id(1L)
                .uuid(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();

        var topic = Topic.builder()
                .title("Test title")
                .content("Test content")
                .author(author)
                .build();

        Topic savedTopic = topicDao.saveTopic(topic);

        assertNotNull(savedTopic.getId());
        assertEquals(topic.getTitle(), savedTopic.getTitle());
        assertEquals(topic.getContent(), savedTopic.getContent());
        assertEquals(topic.getAuthor(), savedTopic.getAuthor());
    }
}
