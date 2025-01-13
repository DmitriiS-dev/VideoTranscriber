package com.videoTranscriber.demo;


import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class AudioStorageService {

    private final Storage storage;

    public AudioStorageService() {
        // Initialize the Google Cloud Storage client
        System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public URI uploadAudio(Path filePath, String bucketName) throws IOException {
        if (!Files.exists(filePath) || !filePath.toString().endsWith(".wav")) {
            throw new IllegalArgumentException("Invalid file. Please upload a valid WAV audio file.");
        }

        String fileName = filePath.getFileName().toString();

        // Define the blob ID and metadata
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("audio/wav").build();

        // Read file bytes and upload the file
        byte[] fileBytes = Files.readAllBytes(filePath);
        Blob blob = storage.create(blobInfo, fileBytes);

        // Return the public URI
        return URI.create(String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName));
    }
}
