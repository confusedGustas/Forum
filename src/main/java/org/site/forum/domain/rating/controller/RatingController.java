package org.site.forum.domain.rating.controller;

import lombok.AllArgsConstructor;
import org.site.forum.domain.rating.service.RatingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RequestMapping("/ratings")
@RestController
@AllArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @PreAuthorize("hasRole('client_user')")
    public void rateTopic(@RequestParam UUID topicId, @RequestParam Integer rating) {
        ratingService.rateTopic(topicId, rating);
    }

}
