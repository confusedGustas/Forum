package org.site.forum.domain.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.dto.response.ReplyResponseDto;
import org.site.forum.domain.comment.integrity.CommentDataIntegrity;
import org.site.forum.domain.comment.service.CommentService;
import org.site.forum.domain.topic.integrity.TopicDataIntegrity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@Tag(name = "Comment Controller", description = "Operations related to comment management")
@RequestMapping("/comments")
@RestController
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentDataIntegrity commentDataIntegrity;
    private final TopicDataIntegrity topicDataIntegrity;

    @PostMapping
    @Operation(
            summary = "Create a new comment",
            description = "Create a new comment or reply to an existing comment"
    )
    public ResponseEntity<ParentCommentResponseDto> saveComment(
            @Parameter(description = "Comment data", required = true)
            @Valid @RequestBody CommentRequestDto commentRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.saveComment(commentRequestDto));
    }

    @GetMapping("/{commentId}")
    @Operation(
            summary = "Get comment by ID",
            description = "Retrieve a specific comment by its ID"
    )
    public ResponseEntity<ReplyResponseDto> getComment(
            @Parameter(description = "UUID of the comment", required = true)
            @PathVariable UUID commentId) {

        commentDataIntegrity.validateCommentId(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentByParent(commentId));
    }

    @DeleteMapping("/{commentId}")
    @Operation(
            summary = "Delete comment",
            description = "Mark a comment as deleted (soft delete)"
    )
    public ResponseEntity<ParentCommentResponseDto> deleteComment(
            @Parameter(description = "UUID of the comment to delete", required = true)
            @PathVariable UUID commentId) {

        commentDataIntegrity.validateCommentId(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.deleteComment(commentId));
    }

    @GetMapping("/topics/{topicId}")
    @Operation(
            summary = "Get comments by topic",
            description = "Retrieve all parent comments for a specific topic"
    )
    public ResponseEntity<Page<ParentCommentResponseDto>> getAllCommentsByTopic(
            @Parameter(description = "UUID of the topic", required = true)
            @PathVariable UUID topicId,
            @Parameter(description = "Page number")
            @RequestParam(required = false) Integer page,
            @Parameter(description = "Number of items per page")
            @RequestParam(required = false) Integer pageSize) {

        topicDataIntegrity.validateTopicId(topicId);
        PageRequest pageRequest = commentDataIntegrity.createValidPageRequest(page, pageSize);
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getAllParentCommentsByTopic(topicId, pageRequest));
    }

    @GetMapping("/{commentId}/replies")
    @Operation(
            summary = "Get replies to a comment",
            description = "Retrieve all replies to a specific parent comment"
    )
    public ResponseEntity<Page<ReplyResponseDto>> getAllRepliesByParent(
            @Parameter(description = "UUID of the parent comment", required = true)
            @PathVariable UUID commentId,
            @Parameter(description = "Page number")
            @RequestParam(required = false) Integer page,
            @Parameter(description = "Number of items per page")
            @RequestParam(required = false) Integer pageSize) {

        commentDataIntegrity.validateCommentId(commentId);
        PageRequest pageRequest = commentDataIntegrity.createValidPageRequest(page, pageSize);
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getAllRepliesByParent(commentId, pageRequest));
    }

}