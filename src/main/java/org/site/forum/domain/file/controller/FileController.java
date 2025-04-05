package org.site.forum.domain.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.site.forum.domain.file.service.FileService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@Tag(name = "File Controller", description = "Operations related to File management")
@RequestMapping("/images")
@RestController
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @DeleteMapping("/delete/{fileId}")
    @PreAuthorize("hasRole('client_user')")
    @Operation(
            summary = "Delete a file by its ID",
            description = "This endpoint deletes a file specified by its UUID. Only users with the 'client_user' role can access this endpoint.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden – User does not have the necessary permissions"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    public void deleteFile(@PathVariable UUID fileId) {
        fileService.deleteFile(fileId);
    }

}
