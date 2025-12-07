package org.site.forum.domain.community.mapper;

import lombok.AllArgsConstructor;
import org.site.forum.domain.community.dto.CommunityDto;
import org.site.forum.domain.community.entity.Community;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommunityMapper {

    public CommunityDto mapToDto(Community community) {
        return CommunityDto.builder()
                .id(community.getId())
                .title(community.getTitle())
                .description(community.getDescription())
                .isEnabled(community.getIsEnabled())
                .createdAt(community.getCreatedAt())
                .updatedAt(community.getUpdatedAt())
                .build();
    }

}
