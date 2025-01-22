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
            throw new InvalidTopicIdException("Topic cannot be null");
        }

        for (MultipartFile file : files) {
            if (!StringUtils.hasText(file.getOriginalFilename())) {
                throw new InvalidFileDataException("File name cannot be null or empty");
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
            throw new InvalidFileIdException("File ID cannot be null");
        }

        File file = fileDao.getFileById(fileId);
        if (file == null) {
            throw new FileNotFoundException("File not found");
        }

        Topic topic = file.getTopic();
        if (topic == null) {
            throw new InvalidTopicIdException("Topic not found");
        }

        checkAuthorization(topic);
        removeFileFromMinio(file.getMinioObjectName());
        fileDao.deleteFile(fileId);
    }

    private void checkAuthorization(Topic topic) {
        if (topic.getAuthor() == null || authenticationService.getAuthenticatedUser() == null) {
            throw new UnauthorizedAccessException("Invalid topic or user authentication");
        }
        if (!topic.getAuthor().getId().equals(authenticationService.getAuthenticatedUser().getId())) {
            throw new UnauthorizedAccessException("You can delete only your files");
        }
    }

    @SneakyThrows
    private void removeFileFromMinio(String minioObjectName) {
        if (!StringUtils.hasText(minioObjectName)) {
            throw new InvalidFileDataException("Minio object name cannot be null or empty");
        }
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(minioObjectName).build());
    }

    @SneakyThrows
    private void uploadFile(MultipartFile file, String generatedFileName) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileDataException("File cannot be null or empty");
        }
        if (!StringUtils.hasText(generatedFileName)) {
            throw new InvalidFileDataException("Generated file name cannot be null or empty");
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
            throw new InvalidFileDataException("File, topic, or generated file name cannot be null or empty");
        }
        File fileEntity = fileMapper.toEntity(file, topic, generatedFileName);
        fileDao.saveFile(fileEntity);
    }

    private String generateFileName(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            throw new InvalidFileDataException("Original file name cannot be null or empty");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return "file_" + UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;
    }
}