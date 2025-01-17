package org.site.forum.domain.topic.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.dao.UserDaoImpl;
import org.site.forum.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TopicDaoImpl.class, UserDaoImpl.class})
public class TopicDaoTests {

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private UserDao userDao;

    private Topic topic;

    @BeforeEach
    public void setUp() {
        User author = User.builder()
                .uuid(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();

        userDao.saveUser(author);

        topic = Topic.builder()
                .title("Test title")
                .content("Test content")
                .author(author)
                .build();
    }

    @Test
    public void testSaveTopic() {
        Topic savedTopic = topicDao.saveTopic(topic);

        assertNotNull(savedTopic.getId());
        assertNotNull(savedTopic.getTitle());
        assertNotNull(savedTopic.getContent());
        assertNotNull(savedTopic.getAuthor());
        assertEquals(topic.getTitle(), savedTopic.getTitle());
        assertEquals(topic.getContent(), savedTopic.getContent());
        assertEquals(topic.getAuthor(), savedTopic.getAuthor());
    }

    @Test
    public void testGetTopic() {
        Topic savedTopic = topicDao.saveTopic(topic);

        Topic foundTopic = topicDao.getTopic(savedTopic.getId());

        assertNotNull(foundTopic.getId());
        assertNotNull(foundTopic.getTitle());
        assertNotNull(foundTopic.getContent());
        assertNotNull(foundTopic.getAuthor());
        assertEquals(savedTopic.getId(), foundTopic.getId());
        assertEquals(savedTopic.getTitle(), foundTopic.getTitle());
        assertEquals(savedTopic.getContent(), foundTopic.getContent());
        assertEquals(savedTopic.getAuthor(), foundTopic.getAuthor());
    }

}
