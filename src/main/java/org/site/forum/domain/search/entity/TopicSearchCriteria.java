package org.site.forum.domain.search.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicSearchCriteria {

    private String search;
    private Integer offset;
    private Integer limit;

    public boolean hasSearch() {
        return search != null && !search.isBlank();
    }

    public int getEffectiveOffset(int defaultValue) {
        return (offset != null) ? offset : defaultValue;
    }

    public int getEffectiveLimit(int defaultValue) {
        return (limit != null) ? limit : defaultValue;
    }

}