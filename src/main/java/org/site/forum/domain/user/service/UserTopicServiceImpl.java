package org.site.forum.domain.user.service;

import lombok.AllArgsConstructor;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class UserTopicServiceImpl implements UserTopicService {

    private final TopicDao topicDao;
    private final FileDao fileDao;
    private final TopicMapper topicMapper;

    @Override
    public Page<TopicResponseDto> getTopicsByUserId(UUID userId, PageRequest pageRequest) {
        var topics = topicDao.getAllTopicsByUserId(userId, pageRequest);
        return topics.map(topic -> topicMapper.toDto(topic, fileDao.findFilesByTopicId(topic.getId())));
    }

}
