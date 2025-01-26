package org.site.forum.domain.search.integrity;

public interface SearchDataIntegrity {

    String validateSortBy(String sortBy);
    String validateSortDirection(String sortDirection);

}
