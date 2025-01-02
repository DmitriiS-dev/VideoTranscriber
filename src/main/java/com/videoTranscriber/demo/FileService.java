package com.videoTranscriber.demo;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    public Path saveFile(MultipartFile file, String directory) throws IOException {

//        Ensures file is not empty:
        if(file.isEmpty()){
            throw new IOException("Uploaded File is Empty");
        }

        // Ensure the directory exists
        if (directory != "uploads"){
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
}
