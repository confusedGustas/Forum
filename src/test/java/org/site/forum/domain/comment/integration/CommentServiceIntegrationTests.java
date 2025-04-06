package org.site.forum.domain.comment.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.repository.CommentRepository;
import org.site.forum.domain.comment.service.CommentService;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.repository.TopicRepository;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class CommentServiceImplIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private User testUser;
    private Topic testTopic;

    @BeforeEach
    void setUp() {
        // Save a test user
        testUser = userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .name("TestUser")
                .build());

        // Save a test topic
        testTopic = topicRepository.save(Topic.builder()
                .title("Integration Testing")
                .content("Test content for topic")
                .author(testUser)
                .build());
    }

    @Test
    @WithMockUser(username = "TestUser")
    void testSaveRootComment() {
        // given
        var request = CommentRequestDto.builder()
                .text("Integration test comment")
                .topicId(testTopic.getId())
                .build();

        // when
        var response = commentService.saveComment(request);

        // then
        assertNotNull(response);
        assertEquals("Integration test comment", response.getText());
        assertEquals(testTopic.getId(), response.getTopicId());
        assertTrue(response.isEnabled());
        assertEquals(testUser.getId(), response.getAuthorId());
    }
}