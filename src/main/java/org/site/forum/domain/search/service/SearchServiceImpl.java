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

    private static final int MAX_LIMIT = 30;
    private static final int DEFAULT_LIMIT = 20;
    private static final int DEFAULT_OFFSET = 0;
    private static final String DEFAULT_SORT_BY = "rating";
    private static final String DEFAULT_SORT_ORDER = "ASC";

    @Override
    public PaginatedResponseDto searchTopics(TopicSearchCriteria criteria) {
        int limit = (criteria.getLimit() == null) ? DEFAULT_LIMIT : criteria.getLimit();
        int offset = (criteria.getOffset() == null) ? DEFAULT_OFFSET : criteria.getOffset();
        String sortBy = (criteria.getSortBy() == null) ? DEFAULT_SORT_BY : criteria.getSortBy();
        String sortOrder = (criteria.getSortDirection() == null) ? DEFAULT_SORT_ORDER : criteria.getSortDirection();

        validateOffsetAndLimit(limit, offset);

        sortBy = searchDataIntegrity.validateSortBy(sortBy);
        sortOrder = searchDataIntegrity.validateSortDirection(sortOrder);

        Pageable pageable = createPageable(offset, limit, sortBy, sortOrder);
        Specification<Topic> specification = topicSpecification.withCriteria(criteria);
        Page<Topic> topicPage = topicRepository.findAll(specification, pageable);
        return paginatedResponseMapper.toDto(topicPage);
    }

    private Pageable createPageable(int offset, int limit, String sortBy, String sortOrder) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        return PageRequest.of(offset, limit, Sort.by(direction, sortBy));
    }

    private void validateOffsetAndLimit(int limit, int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be greater than or equal to 0");
        }
        if (limit <= 0 || limit > MAX_LIMIT) {
            throw new IllegalArgumentException("Limit must be between 1 and " + MAX_LIMIT);
        }
    }
}
