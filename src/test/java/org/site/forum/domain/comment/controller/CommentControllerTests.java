package org.site.forum.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.service.CommentService;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.site.forum.constants.TestConstants.CONTENT;
import static org.site.forum.constants.TestConstants.CREATED_AT;
import static org.site.forum.constants.TestConstants.TITLE;
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class CommentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    private CommentRequestDto commentRequestDto;
    private ParentCommentResponseDto parentCommentResponseDto;
    private Topic topic;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .build();

        topic = Topic.builder()
                .id(UUID.randomUUID())
                .title(TITLE)
                .content(CONTENT)
                .createdAt(CREATED_AT)
                .updatedAt(CREATED_AT)
                .deletedAt(CREATED_AT)
                .isEnabled(true)
                .rating(0)
                .author(user)
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .text(CONTENT)
                .topicId(UUID.fromString(UUID_CONSTANT))
                .parentCommentId(null)
                .build();

        parentCommentResponseDto = ParentCommentResponseDto.builder()
                .id(UUID.randomUUID())
                .text(CONTENT)
                .createdAt(CREATED_AT)
                .isEnabled(true)
                .authorId(user.getId())
                .topicId(topic.getId())
                .build();
    }

    @Test
    void testSaveComment() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(commentRequestDto);

        when(commentService.saveComment(any(CommentRequestDto.class))).thenReturn(parentCommentResponseDto);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(parentCommentResponseDto.getId().toString()))
                .andExpect(jsonPath("$.text").value(parentCommentResponseDto.getText()))
                .andExpect(jsonPath("$.createdAt").value(parentCommentResponseDto.getCreatedAt().toString()))
                .andExpect(jsonPath("$.enabled").value(parentCommentResponseDto.isEnabled()))
                .andExpect(jsonPath("$.authorId").value(parentCommentResponseDto.getAuthorId().toString()))
                .andExpect(jsonPath("$.topicId").value(parentCommentResponseDto.getTopicId().toString()));
    }

    @Test
    void testSaveCommentWithInvalidText() throws Exception {
        commentRequestDto.setText("");
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(commentRequestDto);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testGetAllCommentsByTopicWithInvalidPage() throws Exception {
        mockMvc.perform(get("/comments/topics/{topicId}", UUID.randomUUID())
                        .param("page", "-1")
                        .param("pageSize", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
