package org.site.forum.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.site.forum.common.constant.PageConstant;
import org.site.forum.common.exception.InvalidPageException;
import org.site.forum.common.exception.InvalidPageSizeException;
import org.springframework.data.domain.PageRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageUtils {

    public static PageRequest createValidPageRequest(Integer page, Integer pageSize) {
        int validatedPage = validatePage(page);
        int validatedPageSize = validatePageSize(pageSize);
        return PageRequest.of(validatedPage, validatedPageSize);
    }

    private static int validatePage(Integer page) {
        int normalizedPage = (page == null) ? PageConstant.DEFAULT_PAGE : page;
        if (normalizedPage < 0) {
            throw new InvalidPageException(PageConstant.ERROR_INVALID_PAGE);
        }
        return normalizedPage;
    }

    private static int validatePageSize(Integer pageSize) {
        int normalizedPageSize = (pageSize == null) ? PageConstant.DEFAULT_PAGE_SIZE : pageSize;
        if (normalizedPageSize <= 0 || normalizedPageSize > PageConstant.MAX_PAGE_SIZE) {
            throw new InvalidPageSizeException(PageConstant.ERROR_INVALID_PAGE_SIZE, PageConstant.MAX_PAGE_SIZE);
        }
        return normalizedPageSize;
    }

}
