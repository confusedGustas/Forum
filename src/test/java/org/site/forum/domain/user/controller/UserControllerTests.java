package org.site.forum.domain.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void getAuthenticatedUserComments_ShouldReturnComments_WhenValidRequest() throws Exception {
        int page = 0;
        int pageSize = 10;
        UUID firstCommentId = UUID.randomUUID();
        UUID secondCommentId = UUID.randomUUID();

        List<ParentCommentResponseDto> commentList = List.of(
                new ParentCommentResponseDto(firstCommentId, "Test comment", LocalDateTime.now(), true, UUID.randomUUID(), "test1", UUID.randomUUID()),
                new ParentCommentResponseDto(secondCommentId, "Another comment", LocalDateTime.now(), true, UUID.randomUUID(), "test2", UUID.randomUUID())
        );
        Page<ParentCommentResponseDto> commentPage = new PageImpl<>(commentList);

        when(userService.getAuthenticatedUserComments(PageRequest.of(page, pageSize)))
                .thenReturn(commentPage);

        mockMvc.perform(get("/users/me/comments")
                        .param("page", String.valueOf(page))
                        .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].text").value("Test comment"))
                .andExpect(jsonPath("$.content[1].text").value("Another comment"));

        verify(userService).getAuthenticatedUserComments(PageRequest.of(page, pageSize));
    }

}
