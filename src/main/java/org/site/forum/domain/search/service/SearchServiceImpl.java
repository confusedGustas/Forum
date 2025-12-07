package org.site.forum.domain.search.service;

import lombok.RequiredArgsConstructor;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final TopicRepository topicRepository;
    private final PaginatedResponseMapper paginatedResponseMapper;
    private final TopicSpecification topicSpecification;
    private final SearchDataIntegrity searchDataIntegrity;

    @Override
    public PaginatedResponseDto searchTopics(UUID communityId, TopicSearchCriteria criteria) {
        criteria = searchDataIntegrity.validateAndNormalizeSearchCriteria(criteria);

        Pageable pageable = createPageable(
                criteria.getOffset(),
                criteria.getLimit(),
                criteria.getSortBy(),
                criteria.getSortDirection()
        );

        Specification<Topic> specification = topicSpecification.withCriteria(criteria);

        if (communityId != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("community").get("id"), communityId));
        }

        Page<Topic> topicPage = topicRepository.findAll(specification, pageable);

        return paginatedResponseMapper.toDto(topicPage);
    }


    private Pageable createPageable(int offset, int limit, String sortBy, String sortOrder) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        return PageRequest.of(offset, limit, Sort.by(direction, sortBy));
    }

}