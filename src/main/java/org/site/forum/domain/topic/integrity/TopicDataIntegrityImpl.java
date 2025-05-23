package org.site.forum.domain.topic.integrity;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidFileException;
import org.site.forum.common.exception.InvalidTopicAuthorException;
import org.site.forum.common.exception.InvalidTopicContentException;
import org.site.forum.common.exception.InvalidTopicException;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.InvalidTopicRequestException;
import org.site.forum.common.exception.InvalidTopicTitleException;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.integrity.FileDataIntegrityImpl;
import org.site.forum.domain.file.service.ImageModerationService;
import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.entity.Topic;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TopicDataIntegrityImpl implements TopicDataIntegrity {

    private static final String TOPIC_CANNOT_BE_NULL = "Topic cannot be null";
    private static final String TOPIC_TITLE_EMPTY = "Topic title cannot be empty or null";
    private static final String TOPIC_CONTENT_EMPTY = "Topic content cannot be empty or null";
    private static final String TOPIC_AUTHOR_NULL = "Topic author cannot be null";
    private static final String TOPIC_REQUEST_NULL = "Topic request data cannot be null";
    private static final String TOPIC_ID_NULL = "Topic ID cannot be null";
    private static final String FILE_EMPTY = "File cannot be null or empty";
    public static final String YOU_CAN_UPLOAD_ONLY_5_FILES_PER_TOPIC = "You can upload only 5 files per topic";
    public static final String INVALID_FILE_CONTENT = "Image file contains restricted content";

    private final FileDao fileDao;
    private final ImageModerationService imageModerationService;

    @Override
    public void validateTopicEntity(Topic topic) {
        if (topic == null) {
            throw new InvalidTopicException(TOPIC_CANNOT_BE_NULL);
        }
        if (!StringUtils.hasText(topic.getTitle())) {
            throw new InvalidTopicTitleException(TOPIC_TITLE_EMPTY);
        }
        if (!StringUtils.hasText(topic.getContent())) {
            throw new InvalidTopicContentException(TOPIC_CONTENT_EMPTY);
        }
        if (topic.getAuthor() == null) {
            throw new InvalidTopicAuthorException(TOPIC_AUTHOR_NULL);
        }
    }

    @Override
    public void validateTopicRequestDto(TopicRequestDto dto) {
        if (dto == null) {
            throw new InvalidTopicRequestException(TOPIC_REQUEST_NULL);
        }
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new InvalidTopicRequestException(TOPIC_TITLE_EMPTY);
        }
        if (!StringUtils.hasText(dto.getContent())) {
            throw new InvalidTopicRequestException(TOPIC_CONTENT_EMPTY);
        }
    }

    @Override
    public void validateFiles(List<MultipartFile> files) {
        if (files == null) return;
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new InvalidFileException(FILE_EMPTY);
            }
            if(!imageModerationService.validateImageContent(file)){
                throw new InvalidFileException(INVALID_FILE_CONTENT);
            }
        }
    }

    @Override
    public void validateTopicId(UUID id) {
        if (id == null) {
            throw new InvalidTopicIdException(TOPIC_ID_NULL);
        }
    }

    @Override
    public void validateFileCount(UUID topicId) {
        if (fileDao.fileCountExceedsLimit(topicId)) {
            throw new InvalidFileException(YOU_CAN_UPLOAD_ONLY_5_FILES_PER_TOPIC);
        }
    }

}