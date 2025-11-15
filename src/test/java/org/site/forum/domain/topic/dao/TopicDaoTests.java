package org.site.forum.domain.topic.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.InvalidTopicTitleException;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.service.ImageModerationService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = NONE)
@Import({TopicDaoImpl.class, UserDaoImpl.class, TopicDataIntegrityImpl.class, User.class, Topic.class})
class TopicDaoTests {

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private UserDao userDao;

    @MockitoBean
    private FileDao fileDao;

    @Autowired
    private TestEntityManager entityManager;

    @MockitoBean
    private ImageModerationService imageModerationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.fromString(UUID_CONSTANT))
                .name("test")
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

    @Test
    void testUpdateTopic() {
        Topic topic = Topic.builder()
                .title("Original Title")
                .content("Original Content")
                .author(user)
                .build();
        Topic savedTopic = topicDao.saveTopic(topic);

        Topic updatedTopic = Topic.builder()
                .title("Updated Title")
                .content("Updated Content")
                .author(User.builder().id(UUID.randomUUID()).build())
                .build();

        Topic result = topicDao.updateTopic(savedTopic.getId(), updatedTopic);

        assertNotNull(result.getUpdatedAt());
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Content", result.getContent());
        assertEquals(user.getId(), result.getAuthor().getId());
    }

    @Test
    void testUpdateNonExistentTopicThrowsException() {
        UUID nonExistentId = UUID.randomUUID();
        Topic updatedTopic = Topic.builder()
                .title("Title")
                .content("Content")
                .author(user)
                .build();

        assertThrows(InvalidTopicIdException.class, () ->
                topicDao.updateTopic(nonExistentId, updatedTopic));
    }

}