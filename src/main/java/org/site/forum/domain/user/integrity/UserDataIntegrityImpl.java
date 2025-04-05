package org.site.forum.domain.user.integrity;

import lombok.RequiredArgsConstructor;
import org.site.forum.common.constant.PageConstant;
import org.site.forum.common.exception.*;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDataIntegrityImpl implements UserDataIntegrity {

    public static final String USER_CANNOT_BE_NULL = "User cannot be null";
    public static final String USER_ID_CANNOT_BE_NULL = "User ID cannot be null";

    private final UserDao userDao;

    @Override
    public void validateUser(User user) {
        if (user == null) {
            throw new InvalidUserException(USER_CANNOT_BE_NULL);
        }
    }

    @Override
    public void validateUserId(UUID userId) {
        if (userId == null) {
            throw new InvalidUserIdException(USER_ID_CANNOT_BE_NULL);
        }
    }

    @Override
    public void validateUserNotNull(User user) {
        if (user == null) {
            throw new InvalidUserException(USER_CANNOT_BE_NULL);
        }
    }

    @Override
    public int validatePage(Integer page) {
        int normalizedPage = (page == null) ? PageConstant.DEFAULT_PAGE : page;
        if (normalizedPage < 0) {
            throw new InvalidPageException(PageConstant.ERROR_INVALID_PAGE);
        }
        return normalizedPage;
    }

    @Override
    public int validatePageSize(Integer pageSize) {
        int normalizedPageSize = (pageSize == null) ? PageConstant.DEFAULT_PAGE_SIZE : pageSize;
        if (normalizedPageSize <= 0 || normalizedPageSize > PageConstant.MAX_PAGE_SIZE) {
            throw new InvalidPageSizeException(PageConstant.ERROR_INVALID_PAGE_SIZE, PageConstant.MAX_PAGE_SIZE);
        }
        return normalizedPageSize;
    }

    @Override
    public PageRequest createValidPageRequest(Integer page, Integer pageSize) {
        int validatedPage = validatePage(page);
        int validatedPageSize = validatePageSize(pageSize);
        return PageRequest.of(validatedPage, validatedPageSize);
    }
}