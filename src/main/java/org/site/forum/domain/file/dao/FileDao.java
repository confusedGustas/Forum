package org.site.forum.domain.file.dao;

import org.site.forum.domain.file.entity.File;
import java.util.List;
import java.util.UUID;

public interface FileDao {

    void saveFile(File file);
    List<File> findFilesByTopicId(UUID topicId);
    File getFileById(UUID id);
    void deleteFile(UUID id);
}
