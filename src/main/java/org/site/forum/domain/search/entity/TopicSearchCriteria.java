package org.site.forum.domain.search.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicSearchCriteria {

    private String search;
    private Integer offset;
    private Integer limit;
    private String sortBy;
    private String sortDirection;

}