package com.videoTranscriber.demo.tests;

import com.videoTranscriber.demo.ProcessingService;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VideoProcessingServiceTests {

    private final ProcessingService processingService = new ProcessingService();

    @Test
    void shouldExtractAudioFromVideo() throws Exception{
//        Video File path:
        String testVideoFilePath = "src/test/resources/test-video.mp4";

//        Output Directory:
        String outputDir = "uploads";

        // Extract audio
        String audioFilePath = processingService.extractAudio(testVideoFilePath, outputDir);

//        Verify audio file exists:
        Path audioPath = Paths.get(audioFilePath);
        assertTrue(Files.exists(audioPath));

//        Cleanup:
        Files.delete(audioPath);
    }
}
