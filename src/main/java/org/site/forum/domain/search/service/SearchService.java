package org.site.forum.domain.search.service;

import org.site.forum.domain.search.dto.response.PaginatedResponseDto;
import org.site.forum.domain.search.entity.TopicSearchCriteria;

import java.util.UUID;

public interface SearchService {

    PaginatedResponseDto searchTopics(UUID communityId, TopicSearchCriteria topicSearchCriteria);

}
