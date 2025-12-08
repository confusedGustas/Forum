package org.site.forum.domain.user.service;

import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.UUID;

public interface UserTopicService {

    Page<TopicResponseDto> getTopicsByUserId(UUID userId, PageRequest pageRequest);

}
