package org.site.forum.domain.topic.service;

import lombok.AllArgsConstructor;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.TopicRequestDto;
import org.site.forum.domain.topic.dto.TopicResponseDto;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicDao topicDao;
    private final TopicMapper topicMapper;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Override
    public TopicResponseDto createTopic(TopicRequestDto topicRequestDto) {
        var user = authenticationService.getAuthenticatedUser();
        checkUser(user);

        userService.saveUser(user);

        var topic = topicMapper.topicBuilder(topicRequestDto, user);

        return topicMapper.toDto(topicDao.saveTopic(topic));
    }

    @Override
    public TopicResponseDto getTopic(Long id) {
        return topicMapper.toDto(topicDao.getTopic(id));
    }

    private void checkUser(User user) {
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
    }

}
