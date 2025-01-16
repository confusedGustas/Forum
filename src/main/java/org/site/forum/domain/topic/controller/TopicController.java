package org.site.forum.domain.topic.controller;

import lombok.AllArgsConstructor;
import org.site.forum.domain.topic.dto.TopicRequestDto;
import org.site.forum.domain.topic.dto.TopicResponseDto;
import org.site.forum.domain.topic.service.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/topics")
@RestController
@AllArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @PostMapping
    public ResponseEntity<TopicResponseDto> createTopic(TopicRequestDto topicRequestDto) {
        System.out.println(topicRequestDto.getTitle());
        return ResponseEntity.ok(topicService.createTopic(topicRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDto> getTopic(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.getTopic(id));
    }
}
