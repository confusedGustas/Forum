package org.site.forum.domain.file.dao;

import lombok.AllArgsConstructor;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.file.repository.FileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileDaoImpl implements FileDao {

    private final FileRepository fileRepository;

    @Override
    public void saveFile(File file) {
        if (file.getMinioObjectName() == null || file.getContentType() == null || file.getTopic() == null) {
            throw new IllegalArgumentException("File properties cannot be null");
        }

        fileRepository.save(file);
    }

    @Override
    public List<File> findFilesByTopicId(UUID topicId) {
        return fileRepository.findFilesByTopicId(topicId);
    }

    @Override
    public File getFileById(UUID id) {
        return fileRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("File with the specified id does not exist"));
    }

    @Override
    public void deleteFile(UUID id) {
        fileRepository.deleteById(id);
    }

}
