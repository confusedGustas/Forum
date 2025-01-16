package org.site.forum.domain.topic.controller;

import lombok.AllArgsConstructor;
import org.site.forum.domain.topic.dto.TopicDto;
import org.site.forum.domain.topic.service.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/topics")
@RestController
@AllArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @PostMapping
    public ResponseEntity<TopicDto> createTopic(TopicDto topicDto) {
        return ResponseEntity.ok(topicService.createTopic(topicDto));
    }
}
