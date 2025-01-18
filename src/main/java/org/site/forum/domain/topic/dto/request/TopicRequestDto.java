package org.site.forum.domain.topic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicRequestDto {

    @NotNull
    @NotBlank(message = "Title is mandatory")
    @Size(min = 5, message = "Title must have at least 5 characters")
    private String title;

    @NotNull
    @NotBlank(message = "Content is mandatory")
    @Size(min = 10, message = "Content must have at least 10 characters")
    private String content;

}
