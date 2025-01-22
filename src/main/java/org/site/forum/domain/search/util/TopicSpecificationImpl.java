package org.site.forum.domain.search.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.site.forum.domain.search.entity.TopicSearchCriteria;
import org.site.forum.domain.topic.entity.Topic;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TopicSpecificationImpl implements TopicSpecification {

    public Specification<Topic> withCriteria(TopicSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            if (!criteria.hasSearch()) {
                return criteriaBuilder.conjunction();
            }
            return createSearchPredicate(criteria, criteriaBuilder, root);
        };
    }

    private Predicate createSearchPredicate(TopicSearchCriteria criteria, CriteriaBuilder criteriaBuilder, Root<Topic> root) {
        String searchTerm = wrapWithWildcards(criteria.getSearch());

        Predicate titlePredicate = likeIgnoreCase(criteriaBuilder, root, "title", searchTerm);
        Predicate contentPredicate = likeIgnoreCase(criteriaBuilder, root, "content", searchTerm);

        return criteriaBuilder.or(titlePredicate, contentPredicate);
    }

    private String wrapWithWildcards(String value) {
        return "%" + value.toLowerCase() + "%";
    }

    private Predicate likeIgnoreCase(CriteriaBuilder criteriaBuilder, Root<Topic> root, String field, String searchTerm) {
        return criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), searchTerm);
    }

}
