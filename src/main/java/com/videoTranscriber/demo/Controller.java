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

@RestController
@RequestMapping("/api/videos-upload")
public class Controller {

    @Autowired
    private ExtractAudioService processingService;

    @Autowired
    private FileManipulationServices fileManipulationServices;

    @Autowired
    private AudioStorageService audioStorageService;

    @Autowired
    private TranscribeAudioService transcribeAudioService;

    private final Storage storage;

    private final String uploadDir = "uploads";

    @Value("${gcs.audio-transcription-bucket-project}")
    private String bucketName;
    public Controller() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    /**
     * API: Transcribes File From a Video File:
     */
    @PostMapping("/pathToText")
    public ResponseEntity<String> pathToText(@RequestParam("file") MultipartFile file) throws Exception {

        try {
            // Step 1: Save the video file and retrieve the file path
            Path videoFilePath = fileManipulationServices.saveFile(file, uploadDir);

            // Step 2: Extract the audio from the uploaded video
            String audioFilePath = processingService.extractAudio(videoFilePath.toString(), uploadDir);

            // Step 3: Convert MP3 to Linear16 WAV format
            Path convertedAudioPath = fileManipulationServices.convertMp3ToLinear16(audioFilePath, Path.of(uploadDir));

            // Step 4: Upload the WAV file to GCS and get the URI
            URI gcsUri = audioStorageService.uploadAudio(convertedAudioPath, bucketName);

            // Step 5: Transcribe the audio file and retrieve the text
            String transcription = transcribeAudioService.TranscribeFile(gcsUri);

            // Step 6: Clean up temporary files
            Files.deleteIfExists(convertedAudioPath);
            Files.deleteIfExists(Path.of(audioFilePath));
            Files.deleteIfExists(videoFilePath);


            // Return the transcribed text
            return ResponseEntity.ok("Transcribed Text: " + transcription);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }

    /**
     * API: Transcribes File From a URL:
     */
    @PostMapping("/urlToText")
    public ResponseEntity<String> uploadURL(@RequestParam("url")String url) throws Exception{
        try{
            // Step 1: Download the Audio of the Video inside the URL
            Path audioFilePath = fileManipulationServices.downloadFile(url);

            // Step 2: Convert MP3 to Linear16 WAV format (if needed)
            Path convertedAudioPath = fileManipulationServices.convertMp3ToLinear16(audioFilePath.toString(), Path.of(uploadDir));

            // Step 3: Upload the Audio File to GCS and get the URI:
            URI gcsUri = audioStorageService.uploadAudio(convertedAudioPath, bucketName);

            // Step 5: Transcribe the audio file and retrieve the text:
            String transcription = transcribeAudioService.TranscribeFile(gcsUri);

            // Step 6 Clean Up - remove all the unused files - mp3, wav and audio
            Files.deleteIfExists(convertedAudioPath);
            Files.deleteIfExists(audioFilePath);

            return ResponseEntity.ok("Transcribed Text: " + transcription);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }

    /**
     * API: Uploads files directly to Google Cloud Storage.
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
