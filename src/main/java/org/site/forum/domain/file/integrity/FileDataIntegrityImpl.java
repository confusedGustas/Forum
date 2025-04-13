package org.site.forum.domain.file.integrity;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.FileNotFoundException;
import org.site.forum.common.exception.InvalidFileDataException;
import org.site.forum.common.exception.InvalidFileIdException;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.file.repository.FileRepository;
import org.site.forum.domain.file.service.ImageModerationService;
import org.site.forum.domain.topic.entity.Topic;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileDataIntegrityImpl implements FileDataIntegrity {

    private static final String MINIO_OBJECT_NAME_CANNOT_BE_NULL_OR_EMPTY = "Minio object name cannot be null or empty";
    private static final String CONTENT_TYPE_CANNOT_BE_NULL_OR_EMPTY = "Content type cannot be null or empty";
    private static final String TOPIC_CANNOT_BE_NULL = "Topic cannot be null";
    private static final String TOPIC_ID_CANNOT_BE_NULL = "Topic ID cannot be null";
    private static final String FILE_ID_CANNOT_BE_NULL = "File ID cannot be null";
    private static final String FILE_NOT_FOUND = "File with the specified id does not exist";
    private static final String FILE_NAME_CANNOT_BE_NULL_OR_EMPTY = "File name cannot be null or empty";
    private static final String FILE_CANNOT_BE_NULL_OR_EMPTY = "File cannot be null or empty";
    private static final String GENERATED_FILE_NAME_CANNOT_BE_NULL_OR_EMPTY = "Generated file name cannot be null or empty";
    private static final String FILE_TOPIC_OR_GENERATED_NAME_CANNOT_BE_NULL_OR_EMPTY = "File, topic, or generated file name cannot be null or empty";
    private static final String ORIGINAL_FILE_NAME_CANNOT_BE_NULL_OR_EMPTY = "Original file name cannot be null or empty";
    public static final String FILE_CANNOT_BE_NULL = "File cannot be null";
    public static final String FILE_MUST_HAVE_AN_EXTENSION = "File must have an extension";

    private final FileRepository fileRepository;
    private final ImageModerationService imageModerationService;

    public void validateMinioObjectName(String minioObjectName) {
        if (!StringUtils.hasText(minioObjectName)) {
            throw new InvalidFileDataException(MINIO_OBJECT_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    public void validateContentType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            throw new InvalidFileDataException(CONTENT_TYPE_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    public void validateFileTopic(File file) {
        if (file.getTopic() == null) {
            throw new InvalidFileDataException(TOPIC_CANNOT_BE_NULL);
        }
    }

    public void validateTopicIdNotNull(UUID topicId) {
        if (topicId == null) {
            throw new InvalidTopicIdException(TOPIC_ID_CANNOT_BE_NULL);
        }
    }

    public void validateFileIdNotNull(UUID fileId) {
        if (fileId == null) {
            throw new InvalidFileIdException(FILE_ID_CANNOT_BE_NULL);
        }
    }

    public void validateFileExists(UUID fileId) {
        if (!fileRepository.existsById(fileId)) {
            throw new FileNotFoundException(FILE_NOT_FOUND);
        }
    }

    public void validateFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            throw new InvalidFileDataException(FILE_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    public void validateMultipartFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileDataException(FILE_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    public void validateGeneratedFileName(String generatedFileName) {
        if (!StringUtils.hasText(generatedFileName)) {
            throw new InvalidFileDataException(GENERATED_FILE_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    public void validateFileTopicGeneratedFileName(MultipartFile file, Topic topic, String generatedFileName) {
        if (file == null || topic == null || !StringUtils.hasText(generatedFileName)) {
            throw new InvalidFileDataException(FILE_TOPIC_OR_GENERATED_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public void validateOriginalFileName(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            throw new InvalidFileDataException(ORIGINAL_FILE_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
        if (!originalFilename.contains(".")) {
            throw new InvalidFileDataException(FILE_MUST_HAVE_AN_EXTENSION);
        }
    }

    public void validateTopicNotNull(Topic topic) {
        if (topic == null) {
            throw new InvalidTopicIdException(TOPIC_CANNOT_BE_NULL);
        }
    }
}