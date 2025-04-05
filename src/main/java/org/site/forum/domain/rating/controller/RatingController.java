package org.site.forum.domain.rating.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.site.forum.domain.rating.service.RatingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@Tag(name = "Rating Controller", description = "Operations related to rating forum topics")
@RequestMapping("/ratings")
@RestController
@AllArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @PreAuthorize("hasRole('client_user')")
    @Operation(
            summary = "Rate a topic",
            description = "Allows a user with the role 'client_user' to rate a topic by its UUID"
    )
    public void rateTopic(
            @Parameter(description = "UUID of the topic to be rated", required = true)
            @RequestParam UUID topicId,

            @Parameter(description = "Rating value (e.g. 1 to 5)", required = true)
            @RequestParam Integer rating) {

        ratingService.rateTopic(topicId, rating);
    }

}
