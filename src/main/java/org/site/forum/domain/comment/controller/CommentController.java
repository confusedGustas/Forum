package org.site.forum.domain.comment.controller;

import lombok.AllArgsConstructor;
import org.site.forum.domain.comment.service.CommentService;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.CommentResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/comments")
@RestController
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> saveComment(@RequestBody CommentRequestDto commentRequestDto) {
        return ResponseEntity.ok(commentService.saveComment(commentRequestDto));
    }

}
