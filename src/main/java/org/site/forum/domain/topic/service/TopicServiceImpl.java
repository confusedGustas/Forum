package org.site.forum.domain.topic.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.UnauthorizedAccessException;
import org.site.forum.common.exception.UserNotFoundException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.service.FileService;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.integrity.TopicDataIntegrity;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private static final String TOPIC_NOT_FOUND = "Topic not found";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String UNAUTHORIZED_ACCESS = "You are not authorized to delete this topic";

    private final TopicDao topicDao;
    private final TopicMapper topicMapper;
    private final AuthenticationService authenticationService;
    private final FileService fileService;
    private final FileDao fileDao;
    private final TopicDataIntegrity topicDataIntegrity;
    private final UserDao userDao;
    private final UserService userService;

    @Override
    public TopicResponseDto saveTopic(TopicRequestDto topicRequestDto, List<MultipartFile> files) {
        topicDataIntegrity.validateTopicRequestDto(topicRequestDto);
        topicDataIntegrity.validateFiles(files);

        User user = authenticationService.getAuthenticatedAndPersistedUser();

        Topic topic = topicDao.saveTopic(topicMapper.toEntity(topicRequestDto, user));

        if (files != null && !files.isEmpty()) {
            fileService.uploadFiles(files, topic);
        }

        return topicMapper.toDto(topic, fileDao.findFilesByTopicId(topic.getId()));
    }

    @Override
    @Transactional
    public TopicResponseDto getTopic(UUID id) {
        topicDataIntegrity.validateTopicId(id);

        return topicMapper.toDto(topicDao.getTopic(id), fileDao.findFilesByTopicId(id));
    }

    @Override
    public void deleteTopic(UUID id) {
        topicDataIntegrity.validateTopicId(id);

        Topic topic = topicDao.getTopic(id);
        if (topic == null) throw new InvalidTopicIdException(TOPIC_NOT_FOUND);

        User authenticatedUser = authenticationService.getAuthenticatedUser();
        boolean isOwner = topic.getAuthor() != null && topic.getAuthor().getId().equals(authenticatedUser.getId());
        boolean isAdmin = hasAdminRole();

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
        }

        topicDao.deleteTopic(id);
    }

    private boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        boolean hasAdminAuthority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_client_admin") || authority.equals("ROLE_admin"));
        
        if (hasAdminAuthority) {
            return true;
        }
        
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            Object realmAccessObj = jwt.getClaim("realm_access");
            if (realmAccessObj instanceof Map<?, ?> realmAccess) {
                Object rolesObj = realmAccess.get("roles");
                if (rolesObj instanceof Collection<?> roles) {
                    if (roles.stream().anyMatch("admin"::equals)) {
                        return true;
                    }
                }
            }
            
            Object resourceAccessObj = jwt.getClaim("resource_access");
            if (resourceAccessObj instanceof Map<?, ?> resourceAccess) {
                for (Object clientObj : resourceAccess.values()) {
                    if (clientObj instanceof Map<?, ?> client) {
                        Object clientRolesObj = client.get("roles");
                        if (clientRolesObj instanceof Collection<?> clientRoles) {
                            if (clientRoles.stream().anyMatch("client_admin"::equals)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        
        return false;
    }

    @Override
    public TopicResponseDto updateTopic(UUID id, TopicRequestDto topicRequestDto, List<MultipartFile> files) {
        topicDataIntegrity.validateTopicId(id);
        topicDataIntegrity.validateTopicRequestDto(topicRequestDto);

        User user = getAuthenticatedAndPersistedUser();
        Topic topic = topicDao.updateTopic(id, topicMapper.toEntity(topicRequestDto, user));

        topicDataIntegrity.validateFileCount(id);
        if (files != null && !files.isEmpty()) {
            fileService.uploadFiles(files, topic);
        }

        return topicMapper.toDto(topic, fileDao.findFilesByTopicId(topic.getId()));
    }

    private User getAuthenticatedAndPersistedUser() {
        User user = authenticationService.getAuthenticatedUser();
        if (user == null) throw new UserNotFoundException(USER_NOT_FOUND);
        if (userDao.getUserById(user.getId()).isEmpty()) userService.saveUser(user);
        return user;
    }

}