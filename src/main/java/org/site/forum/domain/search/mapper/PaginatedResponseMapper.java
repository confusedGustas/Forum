package org.site.forum.domain.search.mapper;

import lombok.AllArgsConstructor;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.search.dto.response.PaginatedResponseDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@AllArgsConstructor
public class PaginatedResponseMapper {

    private final TopicMapper topicMapper;
    private final FileDao fileDao;

    public PaginatedResponseDto toDto(Page<Topic> topicPage) {
        List<TopicResponseDto> items = mapTopicsToResponseDto(topicPage);
        return new PaginatedResponseDto(items, topicPage.getNumber(), topicPage.getTotalPages(), topicPage.getTotalElements());
    }

    private List<TopicResponseDto> mapTopicsToResponseDto(Page<Topic> topicPage) {
        return topicPage.getContent().stream()
                .map(this::mapTopicToResponseDto)
                .toList();
    }

    private TopicResponseDto mapTopicToResponseDto(Topic topic) {
        return topicMapper.toDto(topic, fileDao.findFilesByTopicId(topic.getId()));
    }

}