package org.site.forum.domain.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponseDto {

    private List<TopicResponseDto> items;
    private Integer currentPage;
    private Integer totalPages;
    private Long totalItems;

}