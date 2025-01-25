package org.site.forum.domain.topic.mapper;

import lombok.AllArgsConstructor;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.file.mapper.FileMapper;
import org.site.forum.domain.rating.service.RatingService;
import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class TopicMapper {

    private final FileMapper fileMapper;
    private final RatingService ratingService;
    private final AuthenticationService authenticationService;

    public TopicResponseDto toDto(Topic topic, List<File> files) {
        return TopicResponseDto.builder()
                .id(topic.getId())
                .title(topic.getTitle())
                .content(topic.getContent())
                .author(topic.getAuthor())
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())
                .deletedAt(topic.getDeletedAt())
                .isEnabled(topic.getIsEnabled())
                .rating(topic.getRating())
                .files(fileMapper.toDto(files))
                .build();
    }

    public Topic topicBuilder(TopicRequestDto topicRequestDto, User user) {
        return Topic.builder()
                .title(topicRequestDto.getTitle())
                .content(topicRequestDto.getContent())
                .createdAt(LocalDateTime.now())
                .isEnabled(true)
                .rating(0)
                .author(user)
                .build();
    }

}
