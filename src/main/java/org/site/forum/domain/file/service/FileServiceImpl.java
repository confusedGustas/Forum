package org.site.forum.domain.file.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.file.mapper.FileMapper;
import org.site.forum.domain.topic.dao.TopicDao;
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
    private final TopicDao topicDao;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    @SneakyThrows
    public void uploadFiles(List<MultipartFile> files, Topic topic) {
        for (MultipartFile file : files) {
            String generatedFileName = generateFileName(file.getOriginalFilename());

            uploadFile(file, generatedFileName);
            saveFile(file, topic, generatedFileName);
        }
    }

    @Override
    @SneakyThrows
    public void deleteFile(UUID fileId) {
        File file = fileDao.getFileById(fileId);
        Topic topic = topicDao.getTopic(file.getTopic().getId());

        checkAuthorization(topic);
        removeFileFromMinio(file.getMinioObjectName());
        fileDao.deleteFile(fileId);
    }

    private void checkAuthorization(Topic topic) {
        if (!topic.getAuthor().getId().equals(authenticationService.getAuthenticatedUser().getId())) {
            throw new IllegalArgumentException("You can delete only your files");
        }
    }

    @SneakyThrows
    private void removeFileFromMinio(String minioObjectName) {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(minioObjectName).build());
    }

    @SneakyThrows
    private void uploadFile(MultipartFile file, String generatedFileName) {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(generatedFileName)
                .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build()
        );
    }

    private void saveFile(MultipartFile file, Topic topic, String generatedFileName) {
         fileDao.saveFile(fileMapper.toEntity(file, topic, generatedFileName));
    }

    private String generateFileName(String extension) {
        return "file_" + UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + "." + extension;
    }

}
