package org.site.forum.domain.file.integrity;

import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.topic.entity.Topic;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public interface FileDataIntegrity {

    void validateMinioObjectName(String minioObjectName);
    void validateContentType(String contentType);
    void validateFileTopic(File file);
    void validateTopicIdNotNull(UUID topicId);
    void validateFileIdNotNull(UUID fileId);
    void validateFileExists(UUID fileId);
    void validateFileName(String fileName);
    void validateMultipartFile(MultipartFile file);
    void validateGeneratedFileName(String generatedFileName);
    void validateFileTopicGeneratedFileName(MultipartFile file, Topic topic, String generatedFileName);
    void validateOriginalFileName(String originalFilename);
    void validateTopicNotNull(Topic topic);

}
