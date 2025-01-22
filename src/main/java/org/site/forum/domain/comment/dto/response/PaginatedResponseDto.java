package org.site.forum.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponseDto {

    private List<CommentResponseDto> comments;
    private Integer currentPage;
    private Integer totalPages;
    private Long totalItems;

}
