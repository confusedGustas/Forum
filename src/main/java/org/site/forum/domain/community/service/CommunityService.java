package org.site.forum.domain.community.service;

import lombok.RequiredArgsConstructor;
import org.site.forum.domain.community.dto.CommunityDto;
import org.site.forum.domain.community.dto.CommunityRequestDto;
import org.site.forum.domain.community.entity.Community;
import org.site.forum.domain.community.mapper.CommunityMapper;
import org.site.forum.domain.community.repository.CommunityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMapper communityMapper;

    public CommunityDto createCommunity(CommunityRequestDto dto) {
        Community community = new Community();
        community.setTitle(dto.getTitle());
        community.setDescription(dto.getDescription());
        community.setIsEnabled(true);

        Community saved = communityRepository.save(community);
        return communityMapper.mapToDto(saved);
    }

    public List<CommunityDto> getAllCommunities() {
        return communityRepository.findAll()
                .stream()
                .map(communityMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public CommunityDto getCommunityById(UUID id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        return communityMapper.mapToDto(community);
    }

    public CommunityDto updateCommunity(UUID id, CommunityRequestDto dto) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        community.setTitle(dto.getTitle());
        community.setDescription(dto.getDescription());

        Community updated = communityRepository.save(community);
        return communityMapper.mapToDto(updated);
    }

    public void deleteCommunity(UUID id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        communityRepository.delete(community);
    }

}
