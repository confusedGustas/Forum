package org.site.forum.domain.search.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.domain.search.dto.response.PaginatedResponseDto;
import org.site.forum.domain.search.entity.TopicSearchCriteria;
import org.site.forum.domain.search.integrity.SearchDataIntegrity;
import org.site.forum.domain.search.mapper.PaginatedResponseMapper;
import org.site.forum.domain.search.util.TopicSpecification;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.repository.TopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private PaginatedResponseMapper paginatedResponseMapper;

    @Mock
    private TopicSpecification topicSpecification;

    @Mock
    private SearchDataIntegrity searchDataIntegrity;

    @InjectMocks
    private SearchServiceImpl searchService;

    private TopicSearchCriteria criteria;
    private Page<Topic> topicPage;
    private PaginatedResponseDto expectedResponse;

    @BeforeEach
    void setUp() {
        criteria = new TopicSearchCriteria();
        criteria.setSortBy("rating");
        criteria.setSortDirection("DESC");
        criteria.setSearch("test");
        criteria.setOffset(0);
        criteria.setLimit(10);

        Pageable pageable = PageRequest.of(0, 10);
        List<Topic> topics = Collections.singletonList(new Topic());
        topicPage = new PageImpl<>(topics, pageable, topics.size());

        expectedResponse = new PaginatedResponseDto();
        expectedResponse.setItems(Collections.emptyList());
        expectedResponse.setCurrentPage(0);
        expectedResponse.setTotalPages(1);
        expectedResponse.setTotalItems(1L);
    }

    @Test
    void searchTopics_WithValidCriteria_ReturnsPaginatedResponse() {
        TopicSearchCriteria testCriteria = new TopicSearchCriteria();
        testCriteria.setSearch("");
        testCriteria.setSortDirection("ASC");
        testCriteria.setSortBy("rating");
        testCriteria.setOffset(0);
        testCriteria.setLimit(10);

        when(topicSpecification.withCriteria(any(TopicSearchCriteria.class)))
                .thenReturn((Specification<Topic>) (root, query, criteriaBuilder) -> null);
        when(topicRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(topicPage);
        when(paginatedResponseMapper.toDto(topicPage)).thenReturn(expectedResponse);

        PaginatedResponseDto actualResponse = searchService.searchTopics(testCriteria);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void searchTopics_WithEmptySearch_ReturnsAllTopics() {
        TopicSearchCriteria testCriteria = new TopicSearchCriteria();
        testCriteria.setSearch("");
        testCriteria.setSortDirection("DESC");
        testCriteria.setSortBy("date");
        testCriteria.setOffset(0);
        testCriteria.setLimit(10);

        when(topicSpecification.withCriteria(any(TopicSearchCriteria.class)))
                .thenReturn((Specification<Topic>) (root, query, criteriaBuilder) -> null);
        when(topicRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(topicPage);
        when(paginatedResponseMapper.toDto(topicPage)).thenReturn(expectedResponse);

        PaginatedResponseDto actualResponse = searchService.searchTopics(testCriteria);

        assertEquals(expectedResponse, actualResponse);
    }

}