package org.site.forum.domain.comment.dto.request;

import jakarta.annotation.Nullable;
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
public class CommentRequestDto {

    @NotNull
    @NotBlank(message = "Text is mandatory")
    @Size(min = 5, message = "Text must have at least 5 character")
    @Size(max = 600, message = "Text must have at most 600 characters")
    private String text;

    @NotNull
    private UUID topicId;

    @Nullable
    private UUID parentCommentId;

}
