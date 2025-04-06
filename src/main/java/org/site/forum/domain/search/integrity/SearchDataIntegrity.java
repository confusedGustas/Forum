package org.site.forum.domain.search.integrity;

import org.site.forum.domain.search.entity.TopicSearchCriteria;

public interface SearchDataIntegrity {

    String validateSortBy(String sortBy);
    String validateSortDirection(String sortDirection);
    int validateOffset(Integer offset);
    int validateLimit(Integer limit);
    TopicSearchCriteria validateAndNormalizeSearchCriteria(TopicSearchCriteria criteria);

}
