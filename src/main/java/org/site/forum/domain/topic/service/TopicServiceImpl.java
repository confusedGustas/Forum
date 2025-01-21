package org.site.forum.domain.topic.service;

import lombok.AllArgsConstructor;
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
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicDao topicDao;
    private final TopicMapper topicMapper;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final FileService fileService;
    private final UserDao userDao;
    private final FileDao fileDao;

    @Override
    public TopicResponseDto saveTopic(TopicRequestDto topicRequestDto, List<MultipartFile> files) {
        User user = getAuthenticatedAndPersistedUser();
        Topic topic = topicDao.saveTopic(topicMapper.topicBuilder(topicRequestDto, user));

        fileService.uploadFiles(files, topic);

        return topicMapper.toDto(topic, fileDao.findFilesByTopicId(topic.getId()));
    }

    @Override
    public TopicResponseDto getTopic(UUID id) {
        return topicMapper.toDto(topicDao.getTopic(id), fileDao.findFilesByTopicId(id));
    }

    private User getAuthenticatedAndPersistedUser() {
        User user = authenticationService.getAuthenticatedUser();
        checkUser(user);

        if (userDao.getUserById(user.getId()).isEmpty()) {
            userService.saveUser(user);
        }

        return user;
    }

    private void checkUser(User user){
        if(user == null){
            throw new IllegalArgumentException("User not found");
        }
    }

}
