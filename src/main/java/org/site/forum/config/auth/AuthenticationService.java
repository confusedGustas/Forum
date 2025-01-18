package org.site.forum.config.auth;

import lombok.AllArgsConstructor;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.mapper.UserMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserMapper userMapper;

    public User getAuthenticatedUser() {
        return userMapper.toUser((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

}
