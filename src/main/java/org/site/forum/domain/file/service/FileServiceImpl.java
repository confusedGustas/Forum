package org.site.forum.domain.file.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.site.forum.common.exception.FileNotFoundException;
import org.site.forum.common.exception.InvalidFileDataException;
import org.site.forum.common.exception.InvalidFileIdException;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.UnauthorizedAccessException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.file.mapper.FileMapper;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.entity.Topic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final String TOPIC_CANNOT_BE_NULL = "Topic cannot be null";
    private static final String FILE_NAME_CANNOT_BE_NULL_OR_EMPTY = "File name cannot be null or empty";
    private static final String FILE_ID_CANNOT_BE_NULL = "File ID cannot be null";
    private static final String FILE_NOT_FOUND = "File not found";
    private static final String TOPIC_NOT_FOUND = "Topic not found";
    private static final String INVALID_TOPIC_OR_USER_AUTHENTICATION = "Invalid topic or user authentication";
    private static final String CANNOT_DELETE_OTHERS_FILES = "You can delete only your files";
    private static final String MINIO_OBJECT_NAME_CANNOT_BE_NULL_OR_EMPTY = "Minio object name cannot be null or empty";
    private static final String FILE_CANNOT_BE_NULL_OR_EMPTY = "File cannot be null or empty";
    private static final String GENERATED_FILE_NAME_CANNOT_BE_NULL_OR_EMPTY = "Generated file name cannot be null or empty";
    private static final String FILE_TOPIC_OR_GENERATED_NAME_CANNOT_BE_NULL_OR_EMPTY = "File, topic, or generated file name cannot be null or empty";
    private static final String ORIGINAL_FILE_NAME_CANNOT_BE_NULL_OR_EMPTY = "Original file name cannot be null or empty";

    private final MinioClient minioClient;
    private final AuthenticationService authenticationService;
    private final FileMapper fileMapper;
    private final FileDao fileDao;
    private final TopicDao topicDao;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    @SneakyThrows
    public void uploadFiles(List<MultipartFile> files, Topic topic) {
        if (topic == null) {
            throw new InvalidTopicIdException(TOPIC_CANNOT_BE_NULL);
        }

        for (MultipartFile file : files) {
            if (!StringUtils.hasText(file.getOriginalFilename())) {
                throw new InvalidFileDataException(FILE_NAME_CANNOT_BE_NULL_OR_EMPTY);
            }

            String generatedFileName = generateFileName(file.getOriginalFilename());
            uploadFile(file, generatedFileName);
            saveFile(file, topic, generatedFileName);
        }
    }

    @Override
    @SneakyThrows
    public void deleteFile(UUID fileId) {
        if (fileId == null) {
            throw new InvalidFileIdException(FILE_ID_CANNOT_BE_NULL);
        }

        File file = fileDao.getFileById(fileId);
        if (file == null) {
            throw new FileNotFoundException(FILE_NOT_FOUND);
        }

        Topic topic = file.getTopic();
        if (topic == null) {
            throw new InvalidTopicIdException(TOPIC_NOT_FOUND);
        }

        checkAuthorization(topic);
        removeFileFromMinio(file.getMinioObjectName());
        fileDao.deleteFile(fileId);
    }

    private void checkAuthorization(Topic topic) {
        if (topic.getAuthor() == null || authenticationService.getAuthenticatedUser() == null) {
            throw new UnauthorizedAccessException(INVALID_TOPIC_OR_USER_AUTHENTICATION);
        }
        if (!topic.getAuthor().getId().equals(authenticationService.getAuthenticatedUser().getId())) {
            throw new UnauthorizedAccessException(CANNOT_DELETE_OTHERS_FILES);
        }
    }

    @SneakyThrows
    private void removeFileFromMinio(String minioObjectName) {
        if (!StringUtils.hasText(minioObjectName)) {
            throw new InvalidFileDataException(MINIO_OBJECT_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(minioObjectName).build());
    }

    @SneakyThrows
    private void uploadFile(MultipartFile file, String generatedFileName) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileDataException(FILE_CANNOT_BE_NULL_OR_EMPTY);
        }
        if (!StringUtils.hasText(generatedFileName)) {
            throw new InvalidFileDataException(GENERATED_FILE_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(generatedFileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
    }

    private void saveFile(MultipartFile file, Topic topic, String generatedFileName) {
        if (file == null || topic == null || !StringUtils.hasText(generatedFileName)) {
            throw new InvalidFileDataException(FILE_TOPIC_OR_GENERATED_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
        File fileEntity = fileMapper.toEntity(file, topic, generatedFileName);
        fileDao.saveFile(fileEntity);
    }

    private String generateFileName(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            throw new InvalidFileDataException(ORIGINAL_FILE_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return "file_" + UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;
    }

}