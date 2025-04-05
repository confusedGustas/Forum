package org.site.forum.domain.user.integrity;

import org.site.forum.domain.user.entity.User;
import org.springframework.data.domain.PageRequest;
import java.util.UUID;

public interface UserDataIntegrity {

    void validateUser(User user);
    void validateUserId(UUID userId);
    void validateUserNotNull(User user);
    int validatePage(Integer page);
    int validatePageSize(Integer pageSize);
    PageRequest createValidPageRequest(Integer page, Integer pageSize);

}
