package org.site.forum.domain.search.util;

import org.site.forum.domain.search.entity.TopicSearchCriteria;
import org.site.forum.domain.topic.entity.Topic;
import org.springframework.data.jpa.domain.Specification;

public interface TopicSpecification {

    Specification<Topic> withCriteria(TopicSearchCriteria criteria);

}
