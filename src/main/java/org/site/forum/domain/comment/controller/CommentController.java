package org.site.forum.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.CommentResponseDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.service.CommentService;
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

@RequestMapping("/comments")
@RestController
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private static final int MAX_PAGE_SIZE = 50;

    @PostMapping
    public ResponseEntity<CommentResponseDto> saveComment(@Valid @RequestBody CommentRequestDto commentRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.saveComment(commentRequestDto));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> getComment(@PathVariable UUID commentId) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentByParent(commentId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/topics/{topicId}")
    public ResponseEntity<Page<ParentCommentResponseDto>> getAllCommentsByTopic(@PathVariable UUID topicId,
                                                                                @RequestParam int page,
                                                                                @RequestParam int pageSize) {
        if(isPageInvalid(page, pageSize)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllParentCommentsByTopic(topicId, PageRequest.of(page, pageSize)));
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<Page<CommentResponseDto>> getAllRepliesByParent(@PathVariable UUID commentId,
                                                                          @RequestParam int page,
                                                                          @RequestParam int pageSize) {
        if(isPageInvalid(page, pageSize)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllRepliesByParent(commentId, PageRequest.of(page, pageSize)));
    }

    private boolean isPageInvalid(Integer page, Integer pageSize) {
        return page < 0 || pageSize < 0 || pageSize > MAX_PAGE_SIZE;
    }

}
