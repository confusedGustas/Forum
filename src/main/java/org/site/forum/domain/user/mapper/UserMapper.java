package org.site.forum.domain.user.mapper;

import lombok.AllArgsConstructor;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.mapper.CommentMapper;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.user.dto.UserResponseDto;
import org.site.forum.domain.user.entity.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UserMapper {

    private final CommentMapper commentMapper;
    private final TopicMapper topicMapper;

    public User toUser(Jwt jwt) {
        return new User(UUID.fromString(jwt.getClaimAsString("sub")), jwt.getClaimAsString("name"),  null, null);
    }

    public UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .topics(getTopics(user))
                .comments(getComments(user))
                .build();
    }

    private List<ParentCommentResponseDto> getComments(User user) {
        return user.getComments().stream()
                .map(commentMapper::toParentCommentDto)
                .toList();
    }

    private List<TopicResponseDto> getTopics(User user) {
        return user.getTopics().stream()
                .map(topic -> topicMapper.toDto(topic, List.of()))
                .toList();
    }

}
