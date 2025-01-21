package org.site.forum.domain.file.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dao.TopicDaoImpl;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.dao.UserDaoImpl;
import org.site.forum.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({FileDaoImpl.class, UserDaoImpl.class, TopicDaoImpl.class})
class FileDaoTests {

    @Autowired
    private UserDao userDao;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private FileDao fileDao;

    private File file;
    private Topic topic;

    @BeforeEach
    public void setUp() {
        User user = User.builder()
                .id(UUID.fromString(UUID_CONSTANT))
                .build();
        userDao.saveUser(user);

        topic = Topic.builder()
                .title("title")
                .content("content")
                .author(user)
                .build();
        topicDao.saveTopic(topic);
    }

    @Test
    void testSaveFile() {
        file = File.builder()
                .minioObjectName("minioObjectName")
                .contentType("contentType")
                .topic(topic)
                .build();

        fileDao.saveFile(file);

        assertNotNull(file);
        assertNotNull(file.getId());
        assertNotNull(file.getMinioObjectName());
        assertNotNull(file.getContentType());
        assertNotNull(file.getTopic());
    }

    @Test
    void testSaveFileWithoutTopic() {
        file = File.builder()
                .minioObjectName("minioObjectName")
                .contentType("contentType")
                .build();

        assertThrows(Exception.class, () -> fileDao.saveFile(file));
    }

    @Test
    void testSaveFileWithNullValues() {
        file = new File();
        assertThrows(Exception.class, () -> fileDao.saveFile(file));
    }

    @Test
    void testFindFileById() {
        file = File.builder()
                .minioObjectName("minioObjectName")
                .contentType("contentType")
                .topic(topic)
                .build();
        fileDao.saveFile(file);

        Optional<File> retrievedFile = Optional.ofNullable(fileDao.getFileById(file.getId()));
        assertTrue(retrievedFile.isPresent());
        assertEquals(file.getId(), retrievedFile.get().getId());
    }

    @Test
    void testDeleteFile() {
        file = File.builder()
                .minioObjectName("minioObjectName")
                .contentType("contentType")
                .topic(topic)
                .build();
        fileDao.saveFile(file);
        fileDao.deleteFile(file.getId());

        assertThrows(IllegalArgumentException.class,
                () -> Optional.ofNullable(fileDao.getFileById(file.getId()))
                        .orElseThrow(() -> new IllegalArgumentException("File with the specified id does not exist"))
        );
    }

    @Test
    void testFindNonExistingFile() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> fileDao.getFileById(UUID.randomUUID()));
        assertEquals("File with the specified id does not exist", exception.getMessage());
    }
}
