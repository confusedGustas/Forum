package org.site.forum.domain.file.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.site.forum.common.exception.UnauthorizedAccessException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.file.integrity.FileDataIntegrity;
import org.site.forum.domain.file.mapper.FileMapper;
import org.site.forum.domain.topic.entity.Topic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
    private final FileDataIntegrity fileDataIntegrity;

    @Value("${minio.bucket}")
    private String bucket;

    public void uploadFiles(List<MultipartFile> files, Topic topic) {
        fileDataIntegrity.validateTopicNotNull(topic);

        for (MultipartFile file : files) {
            fileDataIntegrity.validateFileName(file.getOriginalFilename());
            String generatedFileName = generateFileName(file.getOriginalFilename());
            uploadFile(file, generatedFileName);
            saveFile(file, topic, generatedFileName);
        }
    }

    public void deleteFile(UUID fileId) {
        fileDataIntegrity.validateFileIdNotNull(fileId);
        fileDataIntegrity.validateFileExists(fileId);
        File file = fileDao.getFileById(fileId);
        Topic topic = file.getTopic();
        fileDataIntegrity.validateTopicNotNull(topic);
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
        fileDataIntegrity.validateMinioObjectName(minioObjectName);
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(minioObjectName).build());
    }

    @SneakyThrows
    private void uploadFile(MultipartFile file, String generatedFileName) {
        fileDataIntegrity.validateMultipartFile(file);
        fileDataIntegrity.validateGeneratedFileName(generatedFileName);

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(generatedFileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
    }

    private void saveFile(MultipartFile file, Topic topic, String generatedFileName) {
        fileDataIntegrity.validateFileTopicGeneratedFileName(file, topic, generatedFileName);
        File fileEntity = fileMapper.toEntity(file, topic, generatedFileName);
        fileDao.saveFile(fileEntity);
    }

    private String generateFileName(String originalFilename) {
        fileDataIntegrity.validateOriginalFileName(originalFilename);
        int extensionIndex = originalFilename.lastIndexOf(".");
        String extension = extensionIndex > 0 ? originalFilename.substring(extensionIndex) : "";
        return "file_" + UUID.randomUUID() + "_" + System.currentTimeMillis() + extension;
    }
}