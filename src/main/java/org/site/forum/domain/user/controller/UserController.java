package org.site.forum.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.site.forum.common.PageUtils;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.user.dto.UserResponseDto;
import org.site.forum.domain.user.integrity.UserDataIntegrity;
import org.site.forum.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@Tag(name = "User Controller", description = "Operations related to user data and activity")
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDataIntegrity userDataIntegrity;

    @GetMapping("/me/comments")
    @Operation(
            summary = "Get authenticated user's comments",
            description = "Retrieve a paginated list of comments made by the currently authenticated user"
    )
    public ResponseEntity<Page<ParentCommentResponseDto>> getAuthenticatedUserComments(
            @Parameter(description = "Page number")
            @RequestParam(required = false) Integer page,
            @Parameter(description = "Number of items per page")
            @RequestParam(required = false) Integer pageSize) {

        PageRequest pageRequest = PageUtils.createValidPageRequest(page, pageSize);
        return ResponseEntity.ok(userService.getAuthenticatedUserComments(pageRequest));
    }

    @GetMapping("/me/topics")
    @Operation(
            summary = "Get authenticated user's topics",
            description = "Retrieve a paginated list of topics created by the currently authenticated user"
    )
    public ResponseEntity<Page<TopicResponseDto>> getAuthenticatedUserTopics(
            @Parameter(description = "Page number")
            @RequestParam(required = false) Integer page,
            @Parameter(description = "Number of items per page")
            @RequestParam(required = false) Integer pageSize) {

        PageRequest pageRequest = PageUtils.createValidPageRequest(page, pageSize);
        return ResponseEntity.ok(userService.getAuthenticatedUserTopics(pageRequest));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieve user profile information by their UUID"
    )
    public ResponseEntity<UserResponseDto> getUserById(
            @Parameter(description = "UUID of the user", required = true)
            @PathVariable UUID id) {

        userDataIntegrity.validateUserId(id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/comments")
    @Operation(
            summary = "Get user's comments by ID",
            description = "Retrieve a paginated list of comments made by a specific user"
    )
    public ResponseEntity<Page<ParentCommentResponseDto>> getUserComments(
            @Parameter(description = "UUID of the user", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Page number")
            @RequestParam(required = false) Integer page,
            @Parameter(description = "Number of items per page")
            @RequestParam(required = false) Integer pageSize) {

        userDataIntegrity.validateUserId(id);
        PageRequest pageRequest = PageUtils.createValidPageRequest(page, pageSize);
        return ResponseEntity.ok(userService.getUserComments(id, pageRequest));
    }

    @GetMapping("/{id}/topics")
    @Operation(
            summary = "Get user's topics by ID",
            description = "Retrieve a paginated list of topics created by a specific user"
    )
    public ResponseEntity<Page<TopicResponseDto>> getUserTopics(
            @Parameter(description = "UUID of the user", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Page number")
            @RequestParam(required = false) Integer page,
            @Parameter(description = "Number of items per page")
            @RequestParam(required = false) Integer pageSize) {

        userDataIntegrity.validateUserId(id);
        PageRequest pageRequest = PageUtils.createValidPageRequest(page, pageSize);
        return ResponseEntity.ok(userService.getUserTopics(id, pageRequest));
    }

}
