package com.videoTranscriber.demo;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


@Service
public class FileManipulationServices {


    /**
     * Saves the Uploaded File and retrieves the path:
     */
    public Path saveFile(MultipartFile file, String directory) throws IOException {

//        Ensures file is not empty:
        if(file.isEmpty()){
            throw new IOException("Uploaded File is Empty");
        }

        // Ensure the directory exists
        if (!directory.equals("uploads")){
            throw new IOException("Invalid Directory!");
        }
        Path uploadDir = Paths.get(directory);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

//        Valid File Type only (only mp4, mkv, and avi allowed):
        String contentType = file.getContentType();
        if (contentType == null ||  (!contentType.equals("video/mp4") && !contentType.equals("video/x-matroska") && !contentType.equals("video/avi"))) {
            throw new IOException("Unsupported file type: " + contentType);
        }

        // Save the file (overwrite if necessary)
        Path filePath = uploadDir.resolve(file.getOriginalFilename());
        Files.write(filePath, file.getBytes());

        return filePath;
    }

    /**
     * Converts mp3 files into (WAV) - LINEAR16
     */
    public Path convertMp3ToLinear16(String mp3FilePath, Path uploadDir) throws IOException, InterruptedException {
//        Output WAV file:
        String outputFilePath = mp3FilePath.replace(".mp3", "-converted.wav");
        Path outputPath = uploadDir.resolve(new File(outputFilePath).getName());

        // Record start time
        long startTime = System.currentTimeMillis();

//        Execute the ffmpeg:
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-i", mp3FilePath, "-ac", "1", "-ar", "16000", "-f", "wav", outputPath.toString()
        );

        Process process = processBuilder.start();

        int exitCode = process.waitFor();
        long endTime = System.currentTimeMillis();

        if (exitCode == 0) {
            System.out.println("Conversion complete: " + outputPath);
            System.out.println("Conversion took: " + (endTime - startTime) + " milliseconds.");
        } else {
            throw new IOException("FFmpeg conversion process failed.");
        }

        return outputPath;
    }

    /**
     * Service to download the file as an audio:
     */
    public Path downloadFile(String url) throws Exception {
        // Validate the URL
        if (url == null || url.isEmpty() || !url.startsWith("https://www.youtube.com")) {
            throw new IllegalArgumentException("Invalid URL provided: " + url);
        }

        // Ensure the download directory exists
        Path downloadDir = Paths.get("uploads");
        if (!Files.exists(downloadDir)) {
            Files.createDirectories(downloadDir);
        }

        // Output file path
        String outputFilePath = downloadDir.resolve("downloaded_audio.mp3").toString();

        // Check if yt-dlp is available
        try {
            Process ytDlpCheck = new ProcessBuilder("yt-dlp", "--version").start();
            if (ytDlpCheck.waitFor() != 0) {
                throw new IOException("yt-dlp is not installed or not available in PATH.");
            }
        } catch (IOException | InterruptedException e) {
            throw new IOException("Failed to verify yt-dlp installation.", e);
        }

        // Construct the yt-dlp command
        List<String> command = Arrays.asList(
                "yt-dlp", "-x", "--audio-format", "mp3", "-o", outputFilePath, url
        );
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        // Redirect error stream for better debugging
        processBuilder.redirectErrorStream(true);

        // Start the process
        Process process = processBuilder.start();

        // Capture process output for debugging
        String errors = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // Wait for the process to complete
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("yt-dlp failed with exit code " + exitCode + ". Output: " + errors);
        }

        // Return the path of the downloaded file
        return Paths.get(outputFilePath);
    }

}
