package org.site.forum.domain.file.dao;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.FileNotFoundException;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.file.integrity.FileDataIntegrity;
import org.site.forum.domain.file.repository.FileRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileDaoImpl implements FileDao {

    private final FileRepository fileRepository;
    private final FileDataIntegrity fileDataIntegrity;

    public void saveFile(File file) {
        fileDataIntegrity.validateMinioObjectName(file.getMinioObjectName());
        fileDataIntegrity.validateContentType(file.getContentType());
        fileDataIntegrity.validateFileTopic(file);
        fileRepository.save(file);
    }

    public List<File> findFilesByTopicId(UUID topicId) {
        fileDataIntegrity.validateTopicIdNotNull(topicId);
        return fileRepository.findFilesByTopicId(topicId);
    }

    public File getFileById(UUID id) {
        fileDataIntegrity.validateFileIdNotNull(id);
        return fileRepository.findById(id).orElseThrow(() -> new FileNotFoundException("File not found"));
    }

    public void deleteFile(UUID id) {
        fileDataIntegrity.validateFileIdNotNull(id);
        fileDataIntegrity.validateFileExists(id);
        fileRepository.deleteById(id);
    }

    @Override
    public Boolean fileCountExceedsLimit(UUID topicId) {
        fileDataIntegrity.validateTopicIdNotNull(topicId);
        return fileRepository.countFilesByTopicId(topicId) >= 5;
    }

}