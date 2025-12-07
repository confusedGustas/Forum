package org.site.forum.domain.topic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.service.TopicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Tag(name = "Topic Controller", description = "Operations related to forum topics")
@RequestMapping("/topics")
@RestController
@AllArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('client_user')")
    @Operation(
            summary = "Create a topic",
            description = "Creates a new topic. Optionally allows file uploads associated with the topic."
    )
    public ResponseEntity<TopicResponseDto> createTopic(
            @Parameter(description = "Title of the topic", required = true)
            @RequestPart("title") String title,

            @Parameter(description = "Content of the topic", required = true)
            @RequestPart("content") String content,

            @RequestPart("communityId") String communityId,

            @Parameter(description = "Optional files to be uploaded with the topic")
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(topicService.saveTopic(TopicRequestDto.builder().title(title).content(content).communityId(UUID.fromString(communityId)).build(), files));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get topic by ID",
            description = "Retrieves a specific topic by its UUID"
    )
    public ResponseEntity<TopicResponseDto> getTopic(
            @Parameter(description = "UUID of the topic", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(topicService.getTopic(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('client_user', 'client_admin')")
    @Operation(
            summary = "Delete a topic",
            description = "Deletes a topic by its UUID. Requires client_user role. Only the topic owner or an admin can delete a topic."
    )
    public ResponseEntity<Void> deleteTopic(
            @Parameter(description = "UUID of the topic to delete", required = true)
            @PathVariable UUID id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('client_user')")
    @Operation(
            summary = "Update a topic",
            description = "Updates an existing topic by its UUID. Allows updating title, content, and files."
    )
    public ResponseEntity<TopicResponseDto> updateTopic(
            @Parameter(description = "UUID of the topic to update", required = true)
            @PathVariable UUID id,

            @Parameter(description = "New title for the topic", required = true)
            @RequestPart("title") String title,

            @Parameter(description = "New content for the topic", required = true)
            @RequestPart("content") String content,

            @Parameter(description = "Optional updated files for the topic")
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        return ResponseEntity.ok(topicService.updateTopic(
                id, TopicRequestDto.builder().title(title).content(content).build(), files)
        );
    }

}
