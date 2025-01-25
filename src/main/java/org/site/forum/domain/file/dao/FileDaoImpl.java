package org.site.forum.domain.file.dao;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.FileNotFoundException;
import org.site.forum.common.exception.InvalidFileDataException;
import org.site.forum.common.exception.InvalidFileIdException;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.file.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileDaoImpl implements FileDao {

    private static final String MINIO_OBJECT_NAME_CANNOT_BE_NULL_OR_EMPTY = "Minio object name cannot be null or empty";
    private static final String CONTENT_TYPE_CANNOT_BE_NULL_OR_EMPTY = "Content type cannot be null or empty";
    private static final String TOPIC_CANNOT_BE_NULL = "Topic cannot be null";
    private static final String TOPIC_ID_CANNOT_BE_NULL = "Topic ID cannot be null";
    private static final String FILE_ID_CANNOT_BE_NULL = "File ID cannot be null";
    private static final String FILE_NOT_FOUND = "File with the specified id does not exist";

    private final FileRepository fileRepository;

    @Override
    public void saveFile(File file) {
        if (!StringUtils.hasText(file.getMinioObjectName())) {
            throw new InvalidFileDataException(MINIO_OBJECT_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }

        if (!StringUtils.hasText(file.getContentType())) {
            throw new InvalidFileDataException(CONTENT_TYPE_CANNOT_BE_NULL_OR_EMPTY);
        }

        if (file.getTopic() == null) {
            throw new InvalidFileDataException(TOPIC_CANNOT_BE_NULL);
        }

        fileRepository.save(file);
    }

    @Override
    public List<File> findFilesByTopicId(UUID topicId) {
        if (topicId == null) {
            throw new InvalidTopicIdException(TOPIC_ID_CANNOT_BE_NULL);
        }

        return fileRepository.findFilesByTopicId(topicId);
    }

    @Override
    public File getFileById(UUID id) {
        if (id == null) {
            throw new InvalidFileIdException(FILE_ID_CANNOT_BE_NULL);
        }

        return fileRepository.findById(id).orElseThrow(() ->
                new FileNotFoundException(FILE_NOT_FOUND));
    }

    @Override
    public void deleteFile(UUID id) {
        if (id == null) {
            throw new InvalidFileIdException(FILE_ID_CANNOT_BE_NULL);
        }

        if (!fileRepository.existsById(id)) {
            throw new FileNotFoundException(FILE_NOT_FOUND);
        }

        fileRepository.deleteById(id);
    }

}