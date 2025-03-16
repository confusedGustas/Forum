package org.site.forum.domain.user.controller;

import lombok.AllArgsConstructor;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.user.dto.UserResponseDto;
import org.site.forum.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/users")
@RestController
@AllArgsConstructor
public class UserController {

    private static final int MAX_PAGE_SIZE = 50;

    private final UserService userService;

    @GetMapping("/me/comments")
    public ResponseEntity<Page<ParentCommentResponseDto>> getAuthenticatedUserComments(@RequestParam int page,
                                                                                       @RequestParam int pageSize) {
        if(isPageInvalid(page, pageSize)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(userService.getAuthenticatedUserComments(PageRequest.of(page, pageSize)));
    }

    @GetMapping("/me/topics")
    public ResponseEntity<Page<TopicResponseDto>> getAuthenticatedUserTopics(@RequestParam int page,
                                                                             @RequestParam int pageSize) {
        if(isPageInvalid(page, pageSize)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(userService.getAuthenticatedUserTopics(PageRequest.of(page, pageSize)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<ParentCommentResponseDto>> getUserComments(@PathVariable UUID id,
                                                                         @RequestParam int page,
                                                                         @RequestParam int pageSize) {
        if(isPageInvalid(page, pageSize)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(userService.getUserComments(id, PageRequest.of(page, pageSize)));
    }

    @GetMapping("/{id}/topics")
    public ResponseEntity<Page<TopicResponseDto>> getUserTopics(@PathVariable UUID id,
                                                               @RequestParam int page,
                                                               @RequestParam int pageSize) {
        if(isPageInvalid(page, pageSize)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(userService.getUserTopics(id, PageRequest.of(page, pageSize)));
    }

    private boolean isPageInvalid(Integer page, Integer pageSize) {
        return page < 0 || pageSize < 0 || pageSize > MAX_PAGE_SIZE;
    }

}
