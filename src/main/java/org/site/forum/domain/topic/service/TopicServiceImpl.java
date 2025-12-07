package org.site.forum.domain.topic.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.UnauthorizedAccessException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.community.entity.Community;
import org.site.forum.domain.community.repository.CommunityRepository;
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
    private static final String UNAUTHORIZED_ACCESS = "You are not authorized to delete this topic";

    private final TopicDao topicDao;
    private final TopicMapper topicMapper;
    private final AuthenticationService authenticationService;
    private final FileService fileService;
    private final FileDao fileDao;
    private final TopicDataIntegrity topicDataIntegrity;
    private final CommunityRepository communityRepository;

    @Override
    public TopicResponseDto saveTopic(TopicRequestDto topicRequestDto, List<MultipartFile> files) {
        topicDataIntegrity.validateTopicRequestDto(topicRequestDto);
        topicDataIntegrity.validateFiles(files);

        User user = authenticationService.getAuthenticatedAndPersistedUser();
        Community community = communityRepository.findById(topicRequestDto.getCommunityId())
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));

        Topic topic = topicMapper.toEntity(topicRequestDto, user);
        topic.setCommunity(community);
        topic = topicDao.saveTopic(topic);

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
        boolean isAdmin = authenticationService.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
        }

        topicDao.deleteTopic(id);
    }

    @Override
    public TopicResponseDto updateTopic(UUID id, TopicRequestDto topicRequestDto, List<MultipartFile> files) {
        topicDataIntegrity.validateTopicId(id);
        topicDataIntegrity.validateTopicRequestDto(topicRequestDto);

        User user = authenticationService.getAuthenticatedAndPersistedUser();
        Topic topic = topicDao.updateTopic(id, topicMapper.toEntity(topicRequestDto, user));

        topicDataIntegrity.validateFileCount(id);
        if (files != null && !files.isEmpty()) {
            fileService.uploadFiles(files, topic);
        }

        return topicMapper.toDto(topic, fileDao.findFilesByTopicId(topic.getId()));
    }

}