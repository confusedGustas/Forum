package org.site.forum.domain.file.controller;

import lombok.AllArgsConstructor;
import org.site.forum.domain.file.service.FileService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RequestMapping("/images")
@RestController
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @DeleteMapping("/delete/{fileId}")
    @PreAuthorize("hasRole('client_user')")
    public void deleteFile(@PathVariable UUID fileId) {
        fileService.deleteFile(fileId);
    }

}
