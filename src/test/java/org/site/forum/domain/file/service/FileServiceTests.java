package org.site.forum.domain.file.service;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.FileNotFoundException;
import org.site.forum.common.exception.InvalidFileDataException;
import org.site.forum.common.exception.InvalidFileIdException;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.UnauthorizedAccessException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.file.integrity.FileDataIntegrity;
import org.site.forum.domain.file.mapper.FileMapper;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTests {

    @Mock private MinioClient minioClient;
    @Mock private AuthenticationService authenticationService;
    @Mock private FileMapper fileMapper;
    @Mock private FileDao fileDao;
    @Mock private FileDataIntegrity fileDataIntegrity;
    @InjectMocks private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "bucket", "test-bucket");
    }

    @Test
    void uploadFiles_TopicIsNull_ThrowsInvalidTopicIdException() {
        List<MultipartFile> files = List.of(mock(MultipartFile.class));
        doThrow(new InvalidTopicIdException("")).when(fileDataIntegrity).validateTopicNotNull(null);
        assertThrows(InvalidTopicIdException.class, () -> fileService.uploadFiles(files, null));
    }

    @Test
    void uploadFiles_InvalidFileName_ThrowsInvalidFileDataException() {
        Topic topic = new Topic();
        MultipartFile invalidFile = mock(MultipartFile.class);
        when(invalidFile.getOriginalFilename()).thenReturn("");
        doThrow(new InvalidFileDataException("")).when(fileDataIntegrity).validateFileName("");
        assertThrows(InvalidFileDataException.class, () -> fileService.uploadFiles(List.of(invalidFile), topic));
    }

    @Test
    void uploadFiles_FileWithoutExtension_ThrowsException() {
        Topic topic = new Topic();
        MultipartFile invalidFile = mock(MultipartFile.class);
        when(invalidFile.getOriginalFilename()).thenReturn("testfile");
        doThrow(new InvalidFileDataException("")).when(fileDataIntegrity).validateOriginalFileName("testfile");
        assertThrows(InvalidFileDataException.class, () -> fileService.uploadFiles(List.of(invalidFile), topic));
    }

    @Test
    void uploadFiles_ValidFiles_CallsMinioAndSavesFiles() throws Exception {
        Topic topic = new Topic();
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);

        when(file1.getOriginalFilename()).thenReturn("test.txt");
        when(file2.getOriginalFilename()).thenReturn("image.jpg");
        when(file1.getContentType()).thenReturn("text/plain");
        when(file2.getContentType()).thenReturn("image/jpeg");
        when(file1.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[0]));
        when(file2.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[0]));
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(mock(ObjectWriteResponse.class));
        when(fileMapper.toEntity(any(), any(), anyString())).thenReturn(new File());

        fileService.uploadFiles(List.of(file1, file2), topic);

        verify(minioClient, times(2)).putObject(any());
        verify(fileDao, times(2)).saveFile(any(File.class));
    }

    @Test
    void uploadFiles_MinioUploadFails_ThrowsException() throws Exception {
        Topic topic = new Topic();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getContentType()).thenReturn("text/plain");
        when(file.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[0]));
        when(minioClient.putObject(any(PutObjectArgs.class))).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> fileService.uploadFiles(List.of(file), topic));
    }

    @Test
    void deleteFile_NullFileId_ThrowsInvalidFileIdException() {
        doThrow(new InvalidFileIdException("")).when(fileDataIntegrity).validateFileIdNotNull(null);
        assertThrows(InvalidFileIdException.class, () -> fileService.deleteFile(null));
    }

    @Test
    void deleteFile_FileNotFound_ThrowsFileNotFoundException() {
        UUID fileId = UUID.randomUUID();
        doThrow(new FileNotFoundException("")).when(fileDataIntegrity).validateFileExists(fileId);
        assertThrows(FileNotFoundException.class, () -> fileService.deleteFile(fileId));
    }


    @Test
    void deleteFile_TopicAuthorNull_ThrowsUnauthorized() {
        UUID fileId = UUID.randomUUID();
        File file = new File();
        file.setTopic(new Topic());

        when(fileDao.getFileById(fileId)).thenReturn(file);

        assertThrows(UnauthorizedAccessException.class, () -> fileService.deleteFile(fileId));
        verify(authenticationService, never()).getAuthenticatedUser();
    }

}