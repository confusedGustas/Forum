package org.site.forum.domain.file.service;

import org.site.forum.domain.topic.entity.Topic;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileService {

    void uploadFiles(List<MultipartFile> files, Topic topic);
    void deleteFile(UUID fileId);

}
