package org.site.forum.domain.topic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.site.forum.domain.file.dto.response.FileResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicResponseDto {

    private UUID id;
    private String title;
    private String content;
    private UUID authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Boolean isEnabled;
    private Integer rating;
    private List<FileResponseDto> files;

}
