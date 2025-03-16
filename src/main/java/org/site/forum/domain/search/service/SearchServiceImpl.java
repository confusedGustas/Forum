package org.site.forum.domain.search.service;

import lombok.AllArgsConstructor;
import org.site.forum.domain.search.dto.response.PaginatedResponseDto;
import org.site.forum.domain.search.entity.TopicSearchCriteria;
import org.site.forum.domain.search.integrity.SearchDataIntegrity;
import org.site.forum.domain.search.mapper.PaginatedResponseMapper;
import org.site.forum.domain.search.util.TopicSpecification;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.repository.TopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final TopicRepository topicRepository;
    private final PaginatedResponseMapper paginatedResponseMapper;
    private final TopicSpecification topicSpecification;
    private final SearchDataIntegrity searchDataIntegrity;

    @Override
    public PaginatedResponseDto searchTopics(TopicSearchCriteria criteria) {
        Pageable pageable = createPageable(criteria);
        Specification<Topic> specification = topicSpecification.withCriteria(criteria);
        Page<Topic> topicPage = topicRepository.findAll(specification, pageable);
        return paginatedResponseMapper.toDto(topicPage);
    }

    private Pageable createPageable(TopicSearchCriteria criteria) {
        String sortDirection = criteria.getSortDirection();
        String sortBy = criteria.getSortBy();
        Sort.Direction direction = Sort.Direction.fromString(sortDirection != null ? sortDirection : "ASC");
        return PageRequest.of(criteria.getOffset(), criteria.getLimit(), Sort.by(direction, sortBy != null ? sortBy : "id"));
    }

}
