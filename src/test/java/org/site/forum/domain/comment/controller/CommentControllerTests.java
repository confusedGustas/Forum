package org.site.forum.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.CommentResponseDto;
import org.site.forum.domain.comment.service.CommentService;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.site.forum.constants.TestConstants.CONTENT;
import static org.site.forum.constants.TestConstants.TITLE;
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CommentController.class)
@ExtendWith(MockitoExtension .class)
public class CommentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;
    private final LocalDateTime createdAt = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        Topic topic = Topic.builder()
                .id(UUID.randomUUID())
                .title(TITLE)
                .content(CONTENT)
                .author(user)
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .text(CONTENT)
                .topicId(UUID.fromString(UUID_CONSTANT))
                .parentCommentId(null)
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(UUID.randomUUID())
                .text(CONTENT)
                .createdAt(createdAt)
                .isEnabled(true)
                .author(user)
                .topic(topic)
                .parentComment(null)
                .build();
    }

    @Test
    public void testSaveComment() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(commentRequestDto);

        when(commentService.saveComment(any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/comments")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId().toString()))
                .andExpect(jsonPath("$.text").value(commentResponseDto.getText()))
                .andExpect(jsonPath("$.createdAt").value(commentResponseDto.getCreatedAt().toString()))
                .andExpect(jsonPath("$.enabled").value(commentResponseDto.isEnabled()))
                .andExpect(jsonPath("$.author.id").value(commentResponseDto.getAuthor().getId().toString()))
                .andExpect(jsonPath("$.topic.id").value(commentResponseDto.getTopic().getId().toString()))
                .andExpect(jsonPath("$.parentComment").value(commentResponseDto.getParentComment()));

    }

}
