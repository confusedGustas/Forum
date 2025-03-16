package org.site.forum.domain.topic.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.service.FileService;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.integrity.TopicDataIntegrity;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private static final String TOPIC_NOT_FOUND = "Topic not found";

    private final TopicDao topicDao;
    private final TopicMapper topicMapper;
    private final AuthenticationService authenticationService;
    private final FileService fileService;
    private final FileDao fileDao;
    private final TopicDataIntegrity topicDataIntegrity;

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

        if (topicDao.getTopic(id) == null) throw new InvalidTopicIdException(TOPIC_NOT_FOUND);

        topicDao.deleteTopic(id);
    }

}