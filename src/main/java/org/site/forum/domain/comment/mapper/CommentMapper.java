package org.site.forum.domain.comment.mapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.dto.response.ReplyResponseDto;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public ParentCommentResponseDto toParentCommentDto(Comment comment) {
        return ParentCommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .isEnabled(comment.isEnabled())
                .authorId(comment.getUser().getId())
                .topicId(comment.getTopic().getId())
                .build();
    }

    public ReplyResponseDto toReplyResponseDto(Comment comment) {
        return ReplyResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getId())
                .topicId(comment.getTopic().getId())
                .parentCommentId(getParentComment(comment))
                .replies(getReplies(comment))
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

    @NotNull
    private List<ReplyResponseDto> getReplies(Comment comment) {
        return comment.getReplies().stream()
                .map(this::toReplyResponseDto)
                .collect(Collectors.toList());
    }

    @Nullable
    private UUID getParentComment(Comment comment) {
        return comment.getParentComment() != null ? comment.getParentComment().getId() : null;
    }

}
