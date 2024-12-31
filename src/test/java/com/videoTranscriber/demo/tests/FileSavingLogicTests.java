package com.videoTranscriber.demo.tests;

import com.videoTranscriber.demo.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileSavingLogicTests {

    private final FileService fileService = new FileService();

    @Test
    void shouldSaveUploadedFile() throws Exception{
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                "dummy video content".getBytes()
        );

        Path savedFilePath = fileService.saveFile(mockFile,"uploads");

        assertTrue(Files.exists(savedFilePath));

//        cleanup:
        Files.delete(savedFilePath);
    }
}
