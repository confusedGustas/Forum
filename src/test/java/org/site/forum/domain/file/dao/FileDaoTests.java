package org.site.forum.domain.file.dao;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.site.forum.common.exception.FileNotFoundException;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dao.TopicDaoImpl;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.integrity.DataIntegrityServiceImpl;
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
@Import({FileDaoImpl.class, UserDaoImpl.class, TopicDaoImpl.class, DataIntegrityServiceImpl.class})
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
    void setUp() {
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

        file = File.builder()
                .minioObjectName("minioObjectName")
                .contentType("contentType")
                .topic(topic)
                .build();
        fileDao.saveFile(file);
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
    @Transactional
    void testSaveFileWithNullValues() {
        file = new File();
        assertThrows(Exception.class, () -> fileDao.saveFile(file));
    }

    @Test
    @Transactional
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
    void testFindNonExistingFile() {
        Exception exception = assertThrows(FileNotFoundException.class, () -> fileDao.getFileById(UUID.randomUUID()));
        assertEquals("File with the specified id does not exist", exception.getMessage());
    }

}
