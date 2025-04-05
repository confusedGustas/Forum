package org.site.forum.domain.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.site.forum.domain.search.dto.response.PaginatedResponseDto;
import org.site.forum.domain.search.entity.TopicSearchCriteria;
import org.site.forum.domain.search.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Search Controller", description = "Operations related to searching forum topics")
@RestController
@RequestMapping("/search")
@AllArgsConstructor
@Validated
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/topics")
    @Operation(
            summary = "Search for forum topics",
            description = "Search for forum topics based on the provided criteria, including optional filters like limit, offset, search term, sorting, etc."
    )
    public ResponseEntity<PaginatedResponseDto> getTopics(
            @Parameter(description = "Maximum number of results to return", required = false)
            @RequestParam(required = false) Integer limit,

            @Parameter(description = "Starting point of the result set", required = false)
            @RequestParam(required = false) Integer offset,

            @Parameter(description = "Search term to filter topics", required = false)
            @RequestParam(required = false) String search,

            @Parameter(description = "Field to sort results by", required = false)
            @RequestParam(required = false) String sortBy,

            @Parameter(description = "Sorting order (asc/desc)", required = false)
            @RequestParam(required = false) String sortOrder) {

        return ResponseEntity.ok(searchService.searchTopics(new TopicSearchCriteria(search, offset, limit, sortBy, sortOrder)));
    }
}
