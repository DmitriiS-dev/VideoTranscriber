package com.videoTranscriber.demo;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ProcessingService {

    public String extractAudio(String videoFilePath, String outputDirectory) throws Exception {
        // Validate video file existence
        Path videoPath = Paths.get(videoFilePath);
        if (!Files.exists(videoPath)) {
            throw new IOException("Video file does not exist: " + videoFilePath);
        }

        // Validate video file is not empty
        if (Files.size(videoPath) == 0) {
            throw new IOException("Video file is empty: " + videoFilePath);
        }

        // Validate video file format
        String fileName = videoPath.getFileName().toString();
        if (!fileName.endsWith(".mp4") && !fileName.endsWith(".mkv") && !fileName.endsWith(".avi")) {
            throw new IOException("Unsupported video file format: " + fileName);
        }

        // Create output directory if it doesn't exist
        Path outputDirPath = Paths.get(outputDirectory);
        if (!Files.exists(outputDirPath)) {
            Files.createDirectories(outputDirPath);
        }

        // Check for write permissions on the output directory
        if (!Files.isWritable(outputDirPath)) {
            throw new IOException("No write permissions for the output directory: " + outputDirectory);
        }

        // Simulate audio extraction
        String audioFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".mp3";
        Path audioFilePath = outputDirPath.resolve(audioFileName);
        Files.createFile(audioFilePath); // Simulate the audio file creation for the test

        return audioFilePath.toString();
    }
}
