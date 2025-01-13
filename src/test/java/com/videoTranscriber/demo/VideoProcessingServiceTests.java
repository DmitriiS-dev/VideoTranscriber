package com.videoTranscriber.demo;

import com.videoTranscriber.demo.ExtractAudioService;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VideoProcessingServiceTests {

    private final ExtractAudioService processingService = new ExtractAudioService();

    @Test
    void shouldExtractAudioFromVideo() throws Exception {
        // Video File path:
        String testVideoFilePath = "src/test/resources/test-video.mp4";

        // Output Directory:
        String outputDir = "uploads";

        // Extract audio
        String audioFilePath = processingService.extractAudio(testVideoFilePath, outputDir);

        // Verify audio file exists:
        Path audioPath = Paths.get(audioFilePath);
        assertTrue(Files.exists(audioPath));

        // Cleanup:
        Files.delete(audioPath);
    }

    @Test
    void shouldHandleNonexistingVideoFile() throws Exception {
        String nonExistentVideoFilePath = "src/test/resources/nonexistent-video.mp4";
        String outputDir = "uploads";

        assertThrows(Exception.class, () -> {
            processingService.extractAudio(nonExistentVideoFilePath, outputDir);
        });
    }

    @Test
    void shouldHandleEmptyVideoFile() throws Exception {
        String emptyVideoFilePath = "src/test/resources/empty-video.mp4";

        Files.createFile(Paths.get(emptyVideoFilePath)); // Empty Video File

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

    // New Test: Should handle large video files (simulating larger size):
    @Test
    void shouldHandleLargeVideoFile() throws Exception {
        // Simulate a large video file (e.g., 2 GB, but smaller for testing)
        byte[] largeVideoContent = new byte[1024 * 1024 * 10]; // Simulating 10 MB for this example
        Path largeFilePath = Paths.get("src/test/resources/large-video.mp4");
        Files.write(largeFilePath, largeVideoContent);

        String outputDir = "uploads";
        assertThrows(Exception.class, () -> {
            processingService.extractAudio(largeFilePath.toString(), outputDir);
        });

        // Cleanup:
        Files.delete(largeFilePath);
    }

    // New Test: Should handle invalid video format despite correct file extension
    @Test
    void shouldHandleInvalidVideoFormatContent() throws Exception {
        // Create a valid video extension but with invalid content
        Path invalidContentFile = Paths.get("src/test/resources/invalid-content-video.mp4");
        Files.writeString(invalidContentFile, "Some corrupt video content");

        String outputDir = "uploads";
        assertThrows(Exception.class, () -> {
            processingService.extractAudio(invalidContentFile.toString(), outputDir);
        });

        // Cleanup:
        Files.delete(invalidContentFile);
    }
}
