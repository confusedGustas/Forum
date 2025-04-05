package org.site.forum.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageConstant {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 50;

    public static final String ERROR_INVALID_PAGE = "Page must be greater than or equal to 0";
    public static final String ERROR_INVALID_PAGE_SIZE = "Page size must be between 1 and " + MAX_PAGE_SIZE;
}