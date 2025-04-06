package org.site.forum.domain.search.integrity;

import lombok.RequiredArgsConstructor;
import org.site.forum.common.exception.InvalidSortDirectionException;
import org.site.forum.common.exception.InvalidSortFieldException;
import org.site.forum.domain.search.entity.TopicSearchCriteria;
import org.springframework.stereotype.Service;

import static org.site.forum.common.constant.SearchConstant.*;

@Service
@RequiredArgsConstructor
public class SearchDataIntegrityImpl implements SearchDataIntegrity {

    @Override
    public String validateSortBy(String sortBy) {
        if (sortBy == null) {
            return DEFAULT_SORT_BY;
        }

        String normalizedSortBy = sortBy.toLowerCase();
        if (!ALLOWED_SORT_FIELDS.contains(normalizedSortBy)) {
            throw new InvalidSortFieldException(sortBy, ALLOWED_SORT_FIELDS);
        }
        return normalizedSortBy;
    }

    @Override
    public String validateSortDirection(String sortDirection) {
        if (sortDirection == null) {
            return DEFAULT_SORT_DIRECTION;
        }

        String normalizedDirection = sortDirection.toUpperCase();
        if (!ALLOWED_SORT_DIRECTIONS.contains(normalizedDirection)) {
            throw new InvalidSortDirectionException(sortDirection, ALLOWED_SORT_DIRECTIONS);
        }
        return normalizedDirection;
    }

    @Override
    public int validateOffset(Integer offset) {
        int normalizedOffset = (offset == null) ? DEFAULT_OFFSET : offset;
        if (normalizedOffset < 0) {
            throw new IllegalArgumentException(ERROR_INVALID_OFFSET);
        }
        return normalizedOffset;
    }

    @Override
    public int validateLimit(Integer limit) {
        int normalizedLimit = (limit == null) ? DEFAULT_LIMIT : limit;
        if (normalizedLimit <= 0 || normalizedLimit > MAX_LIMIT) {
            throw new IllegalArgumentException(ERROR_INVALID_LIMIT);
        }
        return normalizedLimit;
    }

    @Override
    public TopicSearchCriteria validateAndNormalizeSearchCriteria(TopicSearchCriteria criteria) {
        return TopicSearchCriteria.builder()
                .search(criteria.getSearch())
                .offset(validateOffset(criteria.getOffset()))
                .limit(validateLimit(criteria.getLimit()))
                .sortBy(validateSortBy(criteria.getSortBy()))
                .sortDirection(validateSortDirection(criteria.getSortDirection()))
                .build();
    }

}