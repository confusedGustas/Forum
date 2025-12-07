package org.site.forum.domain.topic.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicRequestDto {

    @NotNull
    @NotBlank(message = "Title is mandatory")
    @Size(min = 5, message = "Title must have at least 5 characters")
    @Schema(description = "Title of the topic", example = "My First Topic")
    private String title;

    @NotNull
    @NotBlank(message = "Content is mandatory")
    @Size(min = 10, message = "Content must have at least 10 characters")
    @Schema(description = "Content of the topic", example = "Some content")
    private String content;

    private UUID communityId;

}
