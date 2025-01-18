package org.site.forum.domain.file.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.file.mapper.FileMapper;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FileServiceTests {

    @Mock
    private MinioClient minioClient;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private FileMapper fileMapper;
    @Mock
    private FileDao fileDao;
    @Mock
    private TopicDao topicDao;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FileServiceImpl fileService;

    private Topic topic;
    private User user;
    private File file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(UUID.randomUUID()).build();
        topic = Topic.builder().title("Test Topic").content("Test Content").author(user).build();
        file = File.builder().id(UUID.randomUUID()).minioObjectName("test-file.txt").contentType("text/plain").topic(topic).build();

        ReflectionTestUtils.setField(fileService, "bucket", "test-bucket");
    }

    @Test
    void testUploadFiles_Success() throws Exception {
        when(multipartFile.getOriginalFilename()).thenReturn("test-file.txt");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("text/plain");

        byte[] content = "Test content for file upload".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        when(fileMapper.toEntity(any(), any(), any())).thenReturn(file);

        fileService.uploadFiles(List.of(multipartFile), topic);

        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
        verify(fileDao, times(1)).saveFile(any());
    }

    @Test
    void testDeleteFile_Success() throws Exception {
        when(fileDao.getFileById(file.getId())).thenReturn(file);
        when(topicDao.getTopic(file.getTopic().getId())).thenReturn(topic);
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        assertDoesNotThrow(() -> fileService.deleteFile(file.getId()));

        verify(fileDao, times(1)).deleteFile(file.getId());
        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void testDeleteFile_UnauthorizedUser() {
        User anotherUser = User.builder().id(UUID.randomUUID()).build();
        when(fileDao.getFileById(file.getId())).thenReturn(file);
        when(topicDao.getTopic(file.getTopic().getId())).thenReturn(topic);
        when(authenticationService.getAuthenticatedUser()).thenReturn(anotherUser);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> fileService.deleteFile(file.getId()));
        assertEquals("You can delete only your files", exception.getMessage());

        verify(fileDao, never()).deleteFile(any());
    }

    @Test
    void testDeleteFile_FileNotFound() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        UUID nonExistingFileId = UUID.randomUUID();
        when(fileDao.getFileById(nonExistingFileId)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> fileService.deleteFile(nonExistingFileId));
        assertEquals("File not found", exception.getMessage());

        verify(minioClient, never()).removeObject(any(RemoveObjectArgs.class));
        verify(fileDao, never()).deleteFile(any());
    }

}
