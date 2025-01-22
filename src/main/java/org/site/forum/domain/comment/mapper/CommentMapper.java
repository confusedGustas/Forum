package org.site.forum.domain.comment.mapper;

import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.CommentResponseDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.dto.response.ReplyResponseDto;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public CommentResponseDto toDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .author(comment.getUser())
                .topic(comment.getTopic())
                .parentComment(comment.getParentComment() != null ?
                        toParentCommentResponseDto(comment.getParentComment()) : null)
                .replies(comment.getReplies() != null ? comment.getReplies().stream()
                        .map(this::toReplyResponseDto)
                        .collect(Collectors.toList()) : Collections.emptyList())
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

    private ReplyResponseDto toReplyResponseDto(Comment comment){
        return ReplyResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .isEnabled(comment.isEnabled())
                .userId(comment.getUser().getId())
                .topicId(comment.getTopic().getId())
                .parentCommentId(comment.getParentComment().getId())
                .replies(comment.getReplies().stream()
                        .map(this::toReplyResponseDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private ParentCommentResponseDto toParentCommentResponseDto(Comment comment){
        return ParentCommentResponseDto.builder()
                .id(comment.getId())
                .build();
    }

}
