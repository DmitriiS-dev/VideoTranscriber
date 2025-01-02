package com.videoTranscriber.demo.tests;

import com.videoTranscriber.demo.ProcessingService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void shouldHandleNonexistingVideoFile() throws Exception{
        String nonExistentVideoFilePath = "src/test/resources/nonexistent-video.mp4";
        String outputDir = "uploads";

        assertThrows(Exception.class, () -> {
            processingService.extractAudio(nonExistentVideoFilePath, outputDir);
        });
    }

    @Test
    void shouldHandleEmptyVideoFile() throws Exception{
        String emptyVideoFilePath = "src/test/resources/empty-video.mp4";

        Files.createFile(Paths.get(emptyVideoFilePath)); //Empty Video File

        String outputDir = "uploads";

        assertThrows(Exception.class, () -> {
            processingService.extractAudio(emptyVideoFilePath, outputDir);
        });

        // Cleanup:
        Files.delete(Paths.get(emptyVideoFilePath));
    }

    @Test
    void shouldHandleInvalidVideoFormat() throws Exception {
        String invalidVideoFilePath = "src/test/resources/invalid-format.txt";
        Files.writeString(Paths.get(invalidVideoFilePath), "Invalid video data"); // Create a fake video file

        String outputDir = "uploads";

        assertThrows(Exception.class, () -> {
            processingService.extractAudio(invalidVideoFilePath, outputDir);
        });

        // Cleanup:
        Files.delete(Paths.get(invalidVideoFilePath));
    }

}
