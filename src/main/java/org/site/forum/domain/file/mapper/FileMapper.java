package org.site.forum.domain.file.mapper;

import org.site.forum.domain.file.dto.response.FileResponseDto;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.topic.entity.Topic;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileMapper {

    public File toEntity(MultipartFile file, Topic topic, String generatedFileName) {
        return File.builder()
                .minioObjectName(generatedFileName)
                .contentType(file.getContentType())
                .topic(topic)
                .build();
    }

    public List<FileResponseDto> toDto(List<File> files) {
        return files.stream()
                .map(file -> FileResponseDto.builder()
                        .id(file.getId())
                        .minioObjectName(file.getMinioObjectName())
                        .contentType(file.getContentType())
                        .build())
                .collect(Collectors.toList());
    }

}
