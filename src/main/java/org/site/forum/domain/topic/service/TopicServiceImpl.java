package org.site.forum.domain.topic.service;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidFileException;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.InvalidTopicRequestException;
import org.site.forum.common.exception.UserNotFoundException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.service.FileService;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private static final String TOPIC_ID_CANNOT_BE_NULL = "Topic ID cannot be null";
    private static final String TOPIC_NOT_FOUND = "Topic not found";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String TOPIC_REQUEST_DATA_CANNOT_BE_NULL = "Topic request data cannot be null";
    private static final String TOPIC_TITLE_CANNOT_BE_EMPTY_OR_NULL = "Topic title cannot be empty or null";
    private static final String TOPIC_CONTENT_CANNOT_BE_EMPTY_OR_NULL = "Topic content cannot be empty or null";
    private static final String FILE_CANNOT_BE_NULL_OR_EMPTY = "File cannot be null or empty";

    private final TopicDao topicDao;
    private final TopicMapper topicMapper;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final FileService fileService;
    private final UserDao userDao;
    private final FileDao fileDao;

    @Override
    public TopicResponseDto saveTopic(TopicRequestDto topicRequestDto, List<MultipartFile> files) {
        validateTopicRequestDto(topicRequestDto);

        User user = getAuthenticatedAndPersistedUser();
        Topic topic = topicDao.saveTopic(topicMapper.topicBuilder(topicRequestDto, user));

        if (files != null && !files.isEmpty()) {
            validateFiles(files);
            fileService.uploadFiles(files, topic);
        }

        return topicMapper.toDto(topic, fileDao.findFilesByTopicId(topic.getId()));
    }

    @Override
    public TopicResponseDto getTopic(UUID id) {
        if (id == null) {
            throw new InvalidTopicIdException(TOPIC_ID_CANNOT_BE_NULL);
        }

        return topicMapper.toDto(topicDao.getTopic(id), fileDao.findFilesByTopicId(id));
    }

    @Override
    public void deleteTopic(UUID id) {
        if (id == null) {
            throw new InvalidTopicIdException(TOPIC_ID_CANNOT_BE_NULL);
        }

        if (topicDao.getTopic(id) == null) {
            throw new InvalidTopicIdException(TOPIC_NOT_FOUND);
        }

        topicDao.deleteTopic(id);
    }

    private User getAuthenticatedAndPersistedUser() {
        User user = authenticationService.getAuthenticatedUser();
        checkUser(user);

        if (userDao.getUserById(user.getId()).isEmpty()) {
            userService.saveUser(user);
        }

        return user;
    }

    private void checkUser(User user) {
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
    }

    private void validateTopicRequestDto(TopicRequestDto topicRequestDto) {
        if (topicRequestDto == null) {
            throw new InvalidTopicRequestException(TOPIC_REQUEST_DATA_CANNOT_BE_NULL);
        }

        if (!StringUtils.hasText(topicRequestDto.getTitle())) {
            throw new InvalidTopicRequestException(TOPIC_TITLE_CANNOT_BE_EMPTY_OR_NULL);
        }

        if (!StringUtils.hasText(topicRequestDto.getContent())) {
            throw new InvalidTopicRequestException(TOPIC_CONTENT_CANNOT_BE_EMPTY_OR_NULL);
        }
    }

    private void validateFiles(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new InvalidFileException(FILE_CANNOT_BE_NULL_OR_EMPTY);
            }
        }
    }

}