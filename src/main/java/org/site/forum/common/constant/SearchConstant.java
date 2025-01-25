package org.site.forum.common.constant;

import java.util.List;

public class SearchConstant {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 50;
    public static final List<String> ALLOWED_SORT_FIELDS = List.of("rating", "date");
    public static final List<String> ALLOWED_SORT_DIRECTIONS = List.of("ASC", "DESC");
    public static final String DEFAULT_SORT_BY = "date";
    public static final String DEFAULT_SORT_DIRECTION = "ASC";

}
