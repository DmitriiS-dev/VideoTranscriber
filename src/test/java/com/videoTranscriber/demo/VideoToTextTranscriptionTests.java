package com.videoTranscriber.demo;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VideoToTextTranscriptionTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileManipulationServices fileManipulationServices;

    @Autowired
    private ExtractAudioService processingService;

    private final Path testVideoPath = Paths.get("src/test/resources/test-video.mp4");

    @Test
    @WithMockUser
    void shouldUploadVideosAndGetTranscription() throws Exception {

        // Read the test video file as bytes
        byte[] videoBytes = Files.readAllBytes(testVideoPath);

        MockMultipartFile videoFile = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                videoBytes
        );

        // Perform the upload request to /pathToText endpoint and check that it's OK
        mockMvc.perform(multipart("/api/videos-upload/pathToText")
                        .file(videoFile)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))  // CSRF token included
                .andExpect(status().isOk());  // Expect status 200 OK
    }
}
