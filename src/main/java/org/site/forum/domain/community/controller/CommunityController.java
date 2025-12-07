package org.site.forum.domain.community.controller;

import lombok.RequiredArgsConstructor;
import org.site.forum.domain.community.dto.CommunityDto;
import org.site.forum.domain.community.dto.CommunityRequestDto;
import org.site.forum.domain.community.service.CommunityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping
    public ResponseEntity<CommunityDto> createCommunity(@RequestBody CommunityRequestDto dto) {
        CommunityDto created = communityService.createCommunity(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommunityDto>> getAllCommunities() {
        return ResponseEntity.ok(communityService.getAllCommunities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDto> getCommunityById(@PathVariable UUID id) {
        return ResponseEntity.ok(communityService.getCommunityById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityDto> updateCommunity(@PathVariable UUID id,
                                                        @RequestBody CommunityRequestDto dto) {
        return ResponseEntity.ok(communityService.updateCommunity(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable UUID id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.noContent().build();
    }

}
