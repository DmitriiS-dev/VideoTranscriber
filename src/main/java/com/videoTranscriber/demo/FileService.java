package com.videoTranscriber.demo;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    public Path saveFile(MultipartFile file, String directory) throws IOException{
        Path uploadDir = Paths.get(directory);
        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(file.getOriginalFilename());
        Files.write(filePath, file.getBytes());

        return filePath;
    }
}
