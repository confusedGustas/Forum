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

    private final FileRepository fileRepository;

    @Override
    public void saveFile(File file) {
        if (!StringUtils.hasText(file.getMinioObjectName())) {
            throw new InvalidFileDataException("Minio object name cannot be null or empty");
        }

        if (!StringUtils.hasText(file.getContentType())) {
            throw new InvalidFileDataException("Content type cannot be null or empty");
        }

        if (file.getTopic() == null) {
            throw new InvalidFileDataException("Topic cannot be null");
        }

        fileRepository.save(file);
    }

    @Override
    public List<File> findFilesByTopicId(UUID topicId) {
        if (topicId == null) {
            throw new InvalidTopicIdException("Topic ID cannot be null");
        }

        return fileRepository.findFilesByTopicId(topicId);
    }

    @Override
    public File getFileById(UUID id) {
        if (id == null) {
            throw new InvalidFileIdException("File ID cannot be null");
        }

        return fileRepository.findById(id).orElseThrow(() ->
                new FileNotFoundException("File with the specified id does not exist"));
    }

    @Override
    public void deleteFile(UUID id) {
        if (id == null) {
            throw new InvalidFileIdException("File ID cannot be null");
        }

        if (!fileRepository.existsById(id)) {
            throw new FileNotFoundException("File with the specified id does not exist");
        }

        fileRepository.deleteById(id);
    }
}