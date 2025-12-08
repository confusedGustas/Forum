package org.site.forum.domain.user.service;

import lombok.AllArgsConstructor;
import org.site.forum.domain.comment.dao.CommentDao;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.mapper.CommentMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class UserCommentServiceImpl implements UserCommentService {

    private final CommentDao commentDao;
    private final CommentMapper commentMapper;

    @Override
    public Page<ParentCommentResponseDto> getCommentsByUserId(UUID userId, PageRequest pageRequest) {
        return commentDao.getAllCommentsByUserId(userId, pageRequest).map(commentMapper::toParentCommentDto);
    }

}