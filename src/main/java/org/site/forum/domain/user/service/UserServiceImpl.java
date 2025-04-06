package org.site.forum.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidUserException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.comment.dao.CommentDao;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.mapper.CommentMapper;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.dto.UserResponseDto;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.integrity.UserDataIntegrity;
import org.site.forum.domain.user.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

import static org.site.forum.domain.user.integrity.UserDataIntegrityImpl.USER_CANNOT_BE_NULL;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final FileDao fileDao;
    private final CommentDao commentDao;
    private final TopicDao topicDao;
    private final UserDataIntegrity userDataIntegrity;
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final TopicMapper topicMapper;

    @Override
    public void saveUser(User user) {
        userDataIntegrity.validateUser(user);
        userDao.saveUser(user);
    }

    @Override
    public Page<ParentCommentResponseDto> getAuthenticatedUserComments(PageRequest pageRequest) {
        var user = authenticationService.getAuthenticatedUser();

        var comments = commentDao.getAllCommentsByUserId(user.getId(), pageRequest);

        return comments.map(commentMapper::toParentCommentDto);
    }

    @Override
    public Page<TopicResponseDto> getAuthenticatedUserTopics(PageRequest pageRequest) {
        var user = authenticationService.getAuthenticatedUser();

        var topics = topicDao.getAllTopicsByUserId(user.getId(), pageRequest);

        return topics.map(topic -> topicMapper.toDto(topic, fileDao.findFilesByTopicId(topic.getId())));
    }

    @Override
    public UserResponseDto getUserById(UUID userId) {
        var user = userDao.getUserById(userId)
                .orElseThrow(() -> new InvalidUserException(USER_CANNOT_BE_NULL));

        return userMapper.toUserResponseDto(user);
    }

    @Override
    public Page<ParentCommentResponseDto> getUserComments(UUID userId, PageRequest pageRequest) {
        return commentDao.getAllCommentsByUserId(userId, pageRequest)
                .map(commentMapper::toParentCommentDto);
    }

    @Override
    public Page<TopicResponseDto> getUserTopics(UUID userId, PageRequest pageRequest) {
        return topicDao.getAllTopicsByUserId(userId, pageRequest)
                .map(topic -> topicMapper.toDto(topic, fileDao.findFilesByTopicId(topic.getId())));
    }

}