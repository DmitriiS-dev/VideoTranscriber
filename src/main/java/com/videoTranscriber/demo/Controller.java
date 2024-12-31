package com.videoTranscriber.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/videos-upload")
public class Controller {

    @PostMapping
    public ResponseEntity<String> uploadVideos(@RequestParam("file")MultipartFile file) throws IOException {
//        save the file to a directory:
        Path uploadDir = Paths.get("uploads");

        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(file.getOriginalFilename());
        Files.write(filePath,file.getBytes());


        return ResponseEntity.ok("Video Was Successfully Uploaded");
    }
}
