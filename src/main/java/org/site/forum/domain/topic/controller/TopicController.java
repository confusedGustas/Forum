package org.site.forum.domain.topic.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.service.TopicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@RequestMapping("/topics")
@RestController
@AllArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @PostMapping
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<TopicResponseDto> createTopic(
            @Valid @ModelAttribute("topicRequestDto") TopicRequestDto topicRequestDto,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicService.saveTopic(topicRequestDto, files));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDto> getTopic(@PathVariable UUID id) {
        return ResponseEntity.ok(topicService.getTopic(id));
    }

}
