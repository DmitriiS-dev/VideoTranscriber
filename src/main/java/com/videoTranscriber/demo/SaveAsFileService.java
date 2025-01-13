package com.videoTranscriber.demo;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class SaveAsFileService {


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
}
