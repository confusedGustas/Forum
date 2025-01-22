package org.site.forum.domain.search.service;

import org.site.forum.domain.search.dto.response.PaginatedResponseDto;
import org.site.forum.domain.search.entity.TopicSearchCriteria;

public interface SearchService {

    PaginatedResponseDto searchTopics(TopicSearchCriteria topicSearchCriteria);

}
