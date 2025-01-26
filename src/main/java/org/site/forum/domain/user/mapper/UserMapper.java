package org.site.forum.domain.user.mapper;

import org.site.forum.domain.user.entity.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class UserMapper {

    public User toUser(Jwt jwt) {
        return new User(UUID.fromString(jwt.getClaimAsString("sub")), null, null);
    }

}
