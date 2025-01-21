package org.site.forum.domain.comment.mapper;

import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.CommentResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public CommentResponseDto toDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .author(comment.getUser())
                .topic(comment.getTopic())
                .parentComment(comment.getParentComment())
                .build();
    }

    public Comment toEntity(CommentRequestDto commentRequestDto, User user, Topic topic, Comment parentComment) {
        return Comment.builder()
                .text(commentRequestDto.getText())
                .createdAt(LocalDateTime.now())
                .isEnabled(true)
                .user(user)
                .topic(topic)
                .parentComment(parentComment)
                .build();
    }

}
