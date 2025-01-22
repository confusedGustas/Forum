package org.site.forum.domain.search.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.GlobalExceptionHandler;
import org.site.forum.domain.search.service.SearchService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class SearchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testGetTopicsEmpty_Success() throws Exception {
        mockMvc.perform(get("/api/v1/search/topics"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTopicsFull_Success() throws Exception {
        mockMvc.perform(get("/api/v1/search/topics")
                        .param("limit", "10")
                        .param("offset", "0")
                        .param("search", "topic"))
                .andExpect(status().isOk());
    }

}
