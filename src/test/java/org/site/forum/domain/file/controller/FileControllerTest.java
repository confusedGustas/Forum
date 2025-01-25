package org.site.forum.domain.file.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.FileNotFoundException;
import org.site.forum.common.exception.GlobalExceptionHandler;
import org.site.forum.domain.file.service.FileService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testDeleteFile_Success() throws Exception {
        UUID fileId = UUID.randomUUID();
        doNothing().when(fileService).deleteFile(fileId);

        mockMvc.perform(delete("/api/images/delete/{fileId}", fileId))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteFile_Failure() throws Exception {
        UUID fileId = UUID.randomUUID();
        doThrow(new FileNotFoundException("File not found")).when(fileService).deleteFile(fileId);

        mockMvc.perform(delete("/api/images/delete/{fileId}", fileId))
                .andExpect(status().isNotFound());
    }

}
