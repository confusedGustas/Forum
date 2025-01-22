package org.site.forum.domain.search.service;

import lombok.AllArgsConstructor;
import org.site.forum.domain.search.dto.response.PaginatedResponseDto;
import org.site.forum.domain.search.entity.TopicSearchCriteria;
import org.site.forum.domain.search.mapper.PaginatedResponseMapper;
import org.site.forum.domain.search.util.TopicSpecification;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.repository.TopicRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static org.site.forum.common.constant.SearchConstant.DEFAULT_LIMIT;
import static org.site.forum.common.constant.SearchConstant.DEFAULT_OFFSET;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final TopicRepository topicRepository;
    private final PaginatedResponseMapper paginatedResponseMapper;
    private final TopicSpecification topicSpecification;

    @Override
    public PaginatedResponseDto searchTopics(TopicSearchCriteria topicSearchCriteria) {

        Pageable pageable = createPageable(topicSearchCriteria);
        Specification<Topic> specification = topicSpecification.withCriteria(topicSearchCriteria);

        return paginatedResponseMapper.toDto(topicRepository.findAll(specification, pageable));
    }

    private Pageable createPageable(TopicSearchCriteria criteria) {
        return PageRequest.of(
                criteria.getEffectiveOffset(DEFAULT_OFFSET),
                criteria.getEffectiveLimit(DEFAULT_LIMIT)
        );
    }
}