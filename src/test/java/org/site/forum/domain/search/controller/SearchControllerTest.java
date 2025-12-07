package org.site.forum.domain.search.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.GlobalExceptionHandler;
import org.site.forum.domain.search.integrity.SearchDataIntegrity;
import org.site.forum.domain.search.service.SearchService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    @Mock
    private SearchDataIntegrity searchDataIntegrity;

    private final String dummyCommunityId = "00000000-0000-0000-0000-000000000000";

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetTopicsEmpty_Success() throws Exception {
        mockMvc.perform(get("/search/topics/" + dummyCommunityId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTopicsFull_Success() throws Exception {
        mockMvc.perform(get("/search/topics/" + dummyCommunityId)
                        .param("limit", "10")
                        .param("offset", "0")
                        .param("search", "topic"))
                .andExpect(status().isOk());
    }
}
