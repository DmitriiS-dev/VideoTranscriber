package com.videoTranscriber.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos-upload")
public class Controller {

    @PostMapping
    public ResponseEntity<String> uploadVideos(@RequestParam("file")MultipartFile file){
        return ResponseEntity.ok("Video Was Successfully Uploaded");
    }
}
