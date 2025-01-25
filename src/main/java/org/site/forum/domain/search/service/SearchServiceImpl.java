package org.site.forum.domain.search.service;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.IllegalArgumentException;
import org.site.forum.domain.search.dto.response.PaginatedResponseDto;
import org.site.forum.domain.search.entity.TopicSearchCriteria;
import org.site.forum.domain.search.mapper.PaginatedResponseMapper;
import org.site.forum.domain.search.util.TopicSpecification;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.repository.TopicRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static org.site.forum.common.constant.SearchConstant.ALLOWED_SORT_DIRECTIONS;
import static org.site.forum.common.constant.SearchConstant.ALLOWED_SORT_FIELDS;
import static org.site.forum.common.constant.SearchConstant.DEFAULT_LIMIT;
import static org.site.forum.common.constant.SearchConstant.DEFAULT_OFFSET;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private static final String INVALID_SORT_FIELD_MESSAGE = "Invalid sort field: ";
    private static final String INVALID_SORT_DIRECTION_MESSAGE = "Invalid sort direction: ";

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
        return PageRequest.of(getValidOffset(criteria), getValidLimit(criteria), createSort(criteria));
    }

    private int getValidOffset(TopicSearchCriteria criteria) {
        return criteria.getEffectiveOffset(DEFAULT_OFFSET);
    }

    private int getValidLimit(TopicSearchCriteria criteria) {
        return criteria.getEffectiveLimit(DEFAULT_LIMIT);
    }

    private Sort createSort(TopicSearchCriteria criteria) {
        Sort.Direction direction = Sort.Direction.fromString(validateSortDirection(criteria));
        return Sort.by(direction, validateSortBy(criteria));
    }

    private String validateSortBy(TopicSearchCriteria criteria) {
        String sortBy = criteria.getSortBy();
        if (sortBy == null || !ALLOWED_SORT_FIELDS.contains(sortBy.toLowerCase())) {
            throw new IllegalArgumentException(INVALID_SORT_FIELD_MESSAGE + sortBy);
        }
        return sortBy.toLowerCase();
    }

    private String validateSortDirection(TopicSearchCriteria criteria) {
        String sortDirection = criteria.getSortDirection();
        if (sortDirection == null || !ALLOWED_SORT_DIRECTIONS.contains(sortDirection.toUpperCase())) {
            throw new IllegalArgumentException(INVALID_SORT_DIRECTION_MESSAGE + sortDirection);
        }
        return sortDirection.toUpperCase();
    }

}