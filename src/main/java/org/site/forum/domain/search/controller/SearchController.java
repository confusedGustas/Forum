package org.site.forum.domain.search.controller;

import lombok.AllArgsConstructor;
import org.site.forum.domain.search.dto.response.PaginatedResponseDto;
import org.site.forum.domain.search.entity.TopicSearchCriteria;
import org.site.forum.domain.search.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@AllArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/topics")
    public ResponseEntity<PaginatedResponseDto> getGames(@RequestParam(required = false) Integer limit,
                                                         @RequestParam(required = false) Integer offset,
                                                         @RequestParam(required = false) String search,
                                                         @RequestParam(required = false) String sortBy,
                                                         @RequestParam(required = false) String sortOrder) {
        return ResponseEntity.ok(searchService.searchTopics(
                new TopicSearchCriteria(search, offset, limit, sortBy, sortOrder)));
    }

}