package org.site.forum.domain.topic.service;

import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

public interface TopicService {

    TopicResponseDto createTopic(TopicRequestDto topicRequestDto, List<MultipartFile> files);
    TopicResponseDto getTopic(UUID id);

}
