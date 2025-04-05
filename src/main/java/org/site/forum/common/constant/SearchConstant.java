package org.site.forum.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchConstant {
    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 20;
    public static final int MAX_LIMIT = 50;

    public static final Set<String> ALLOWED_SORT_FIELDS = Set.of("rating", "title");
    public static final Set<String> ALLOWED_SORT_DIRECTIONS = Set.of("ASC", "DESC");

    public static final String DEFAULT_SORT_BY = "rating";
    public static final String DEFAULT_SORT_DIRECTION = "ASC";

    public static final String ERROR_INVALID_OFFSET = "Offset must be greater than or equal to 0";
    public static final String ERROR_INVALID_LIMIT = "Limit must be between 1 and " + MAX_LIMIT;
    public static final String ERROR_INVALID_SORT_FIELD = "Invalid sort field: ";
    public static final String ERROR_INVALID_SORT_DIRECTION = "Invalid sort direction: ";
}