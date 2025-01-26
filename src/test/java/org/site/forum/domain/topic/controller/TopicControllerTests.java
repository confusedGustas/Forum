package org.site.forum.domain.topic.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.service.TopicService;
import org.site.forum.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.site.forum.constants.TestConstants.CONTENT;
import static org.site.forum.constants.TestConstants.TITLE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TopicController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TopicControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TopicService topicService;

    private TopicResponseDto topicResponseDto;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        topicResponseDto = TopicResponseDto.builder()
                .id(UUID.randomUUID())
                .title(TITLE)
                .content(CONTENT)
                .author(user)
                .build();
    }

    @Test
    void testCreateTopic() throws Exception {
        when(topicService.saveTopic(any(TopicRequestDto.class), eq(null))).thenReturn(topicResponseDto);

        mockMvc.perform(multipart("/topics")
                .param("title", TITLE)
                .param("content", CONTENT)
                .contentType("multipart/form-data"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(topicResponseDto.getId().toString()))
                .andExpect(jsonPath("$.title").value(topicResponseDto.getTitle()))
                .andExpect(jsonPath("$.content").value(topicResponseDto.getContent()))
                .andExpect(jsonPath("$.author.id").value(topicResponseDto.getAuthor().getId().toString()));
    }

    @Test
    void testUpdateTopic() throws Exception {
        UUID topicId = UUID.randomUUID();
        when(topicService.updateTopic(eq(topicId), any(TopicRequestDto.class), eq(null)))
                .thenReturn(topicResponseDto);

        mockMvc.perform(multipart("/topics/update/{id}", topicId)
                        .param("title", TITLE)
                        .param("content", CONTENT)
                        .contentType("multipart/form-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(topicResponseDto.getId().toString()))
                .andExpect(jsonPath("$.title").value(topicResponseDto.getTitle()))
                .andExpect(jsonPath("$.content").value(topicResponseDto.getContent()));
    }


}
