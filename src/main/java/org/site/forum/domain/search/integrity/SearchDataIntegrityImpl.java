package org.site.forum.domain.search.integrity;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static org.site.forum.common.constant.SearchConstant.ALLOWED_SORT_DIRECTIONS;
import static org.site.forum.common.constant.SearchConstant.ALLOWED_SORT_FIELDS;

@Service
@AllArgsConstructor
public class SearchDataIntegrityImpl implements SearchDataIntegrity {

    private static final String INVALID_SORT_FIELD_MESSAGE = "Invalid sort field: ";
    private static final String INVALID_SORT_DIRECTION_MESSAGE = "Invalid sort direction: ";

    public String validateSortBy(String sortBy) {
        if (sortBy == null || !ALLOWED_SORT_FIELDS.contains(sortBy.toLowerCase())) {
            throw new IllegalArgumentException(INVALID_SORT_FIELD_MESSAGE + sortBy);
        }
        return sortBy.toLowerCase();
    }

    public String validateSortDirection(String sortDirection) {
        if (sortDirection == null || !ALLOWED_SORT_DIRECTIONS.contains(sortDirection.toUpperCase())) {
            throw new IllegalArgumentException(INVALID_SORT_DIRECTION_MESSAGE + sortDirection);
        }
        return sortDirection.toUpperCase();
    }

}
