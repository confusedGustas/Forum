package org.site.forum.domain.topic.integrity;

import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.entity.Topic;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

public interface TopicDataIntegrity {

    void validateTopicEntity(Topic topic);
    void validateTopicRequestDto(TopicRequestDto dto);
    void validateFiles(List<MultipartFile> files);
    void validateTopicId(UUID id);

}