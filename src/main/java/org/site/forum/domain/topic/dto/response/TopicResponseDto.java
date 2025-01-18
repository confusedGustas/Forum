package org.site.forum.domain.topic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.site.forum.domain.file.dto.response.FileResponseDto;
import org.site.forum.domain.user.entity.User;
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
    private User author;
    private List<FileResponseDto> files;

}
