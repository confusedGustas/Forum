package org.site.forum.domain.user.service;

import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.user.dto.UserResponseDto;
import org.site.forum.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.UUID;

public interface UserService {

    void saveUser(User user);
    UserResponseDto getUserById(UUID id);
    Page<ParentCommentResponseDto> getAuthenticatedUserComments(PageRequest pageRequest);
    Page<TopicResponseDto> getAuthenticatedUserTopics(PageRequest pageRequest);
    Page<ParentCommentResponseDto> getUserComments(UUID id, PageRequest pageRequest);
    Page<TopicResponseDto> getUserTopics(UUID id, PageRequest pageRequest);

}
