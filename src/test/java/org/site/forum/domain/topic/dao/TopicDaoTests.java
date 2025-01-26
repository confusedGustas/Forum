package org.site.forum.domain.topic.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.InvalidTopicTitleException;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.integrity.TopicDataIntegrityImpl;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.dao.UserDaoImpl;
import org.site.forum.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = NONE)
@Import({TopicDaoImpl.class, UserDaoImpl.class, TopicDataIntegrityImpl.class})
class TopicDaoTests {

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.fromString(UUID_CONSTANT))
                .build();
        user = userDao.saveUser(user);
        entityManager.flush();
    }

    @Test
    void testSaveTopic() {
        Topic topic = Topic.builder()
                .title("Valid Title")
                .content("Test Content")
                .author(user)
                .build();

        Topic savedTopic = topicDao.saveTopic(topic);

        assertNotNull(savedTopic.getId());
        assertEquals("Valid Title", savedTopic.getTitle());
        assertEquals(user.getId(), savedTopic.getAuthor().getId());
    }

    @Test
    void testGetTopicWhenNotFound() {
        UUID randomId = UUID.randomUUID();
        assertThrows(InvalidTopicIdException.class,
                () -> topicDao.getTopic(randomId),
                "Should throw for non-existent ID"
        );
    }

    @Test
    void testDeleteTopic() {
        Topic topic = Topic.builder()
                .title("To Delete")
                .content("Content")
                .author(user)
                .build();
        Topic savedTopic = topicDao.saveTopic(topic);

        topicDao.deleteTopic(savedTopic.getId());
        Topic deletedTopic = topicDao.getTopic(savedTopic.getId());

        assertEquals("This topic has been deleted", deletedTopic.getTitle());
        assertNotNull(deletedTopic.getDeletedAt());
    }

    @Test
    void testInvalidTopicTitle() {
        Topic invalidTopic = Topic.builder()
                .title("")
                .content("Content")
                .author(user)
                .build();

        assertThrows(InvalidTopicTitleException.class,
                () -> topicDao.saveTopic(invalidTopic),
                "Should reject empty title"
        );
    }

}