package com.videoTranscriber.demo;

import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
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

    @Autowired
    private AudioTranscriber audioTranscriber;

    private final Storage storage;

    @Value("${gcs.audio-transcription-bucket-project")
    private String bucketName;
    public Controller(){
        this.storage = StorageOptions.getDefaultInstance().getService();
    }
    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadVideos(@RequestParam("file")MultipartFile file) throws Exception {
        String uploadDir = "uploads";

//      Step 1: Save the video file and retrieve the file path
        var videoFilePath = fileService.saveFile(file, uploadDir);

//      Step 2: Extract the audio from the uploaded video
        String audioFilePath = processingService.extractAudio(videoFilePath.toString(), "uploads");

        // Step 3: Convert MP3 to Linear16 WAV format (if needed)
        Path convertedAudioPath = fileService.convertMp3ToLinear16(audioFilePath, Path.of(uploadDir));

        // Step 4: Transcribe the audio file
        String transcription = audioTranscriber.transcribeAudio(convertedAudioPath.toString());



        return ResponseEntity.ok("Transcribed Text from the audio file "+ transcription);
    }

//    Send a URL:
    @PostMapping("uploadURL")
    public ResponseEntity<String> uploadURL(@RequestParam("url")String url) throws Exception{

//        Send it over to the Transcribe Audio File:
        String transcription = audioTranscriber.transcribeYouTubeAudio(url);

        return ResponseEntity.ok("Transcribed URL "+transcription);
    }


    /**
     * New API: Upload files directly to Google Cloud Storage.
     */
    @PostMapping("/uploadToGCS")
    public ResponseEntity<String> uploadToGCS(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty. Please upload a valid file.");
            }

            String fileName = file.getOriginalFilename();

            // Define BlobId and BlobInfo
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

            // Upload the file to the bucket
            Blob blob = storage.create(blobInfo, file.getBytes());

            // Generate the public URL for the uploaded file
            URI uri = URI.create(String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName));

            return ResponseEntity.ok("File uploaded to GCS successfully: " + uri.toString());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload to GCS failed: " + e.getMessage());
        }
    }
}
