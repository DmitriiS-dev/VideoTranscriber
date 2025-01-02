package com.videoTranscriber.demo;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ProcessingService processingService;

    @Autowired
    private FileService fileService;

    @PostMapping
    public ResponseEntity<String> uploadVideos(@RequestParam("file")MultipartFile file) throws Exception {
        String uploadDir = "uploads";

//      Save the Video File and Retrieve the File Path:
        var videoFilePath = fileService.saveFile(file, uploadDir);

//      Extract the Audio from the uploaded Video:
        String audioFilePath = processingService.extractAudio(videoFilePath.toString(), "uploads");

        // Convert audio and transcribe to text:
        Path convertedAudioPath = fileService.convertMp3ToLinear16(audioFilePath, Path.of(uploadDir));




        return ResponseEntity.ok("Audio Was Successfully Extracted "+ audioFilePath);
    }
}
