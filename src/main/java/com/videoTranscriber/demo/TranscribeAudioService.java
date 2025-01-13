package com.videoTranscriber.demo;


import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;


@Service
public class TranscribeAudioService {


    private final Storage storage;

    public TranscribeAudioService(){
        // Initialize the Google Cloud Storage client
        System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    /**
     * Transcribe the Audio File Inside the GCS
     */
    public String TranscribeFile(URI uri) {
        String bucketName = "audio-transcription-bucket-project"; // Hardcoded or from configuration
        String objectName = uri.getPath().substring(bucketName.length() + 2); // Remove leading '/' and bucket name
        String gcsUri = "gs://" + bucketName + "/" + objectName;
        System.out.println("Transcribing file: " + gcsUri);

        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.LINEAR16)
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(16000)
                    .build();

            // Use LongRunningRecognize for audio files >1 minute
            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speechClient.longRunningRecognizeAsync(config, audio);

            // Wait for the operation to complete
            LongRunningRecognizeResponse result = response.get();

            StringBuilder transcription = new StringBuilder();
            for (SpeechRecognitionResult res : result.getResultsList()) {
                transcription.append(res.getAlternatives(0).getTranscript()).append(" ");
            }

            System.out.println("Transcription: " + transcription);
            return transcription.toString().trim();
        } catch (Exception e) {
            throw new RuntimeException("Error during transcription: " + e.getMessage(), e);
        } finally {
            boolean isCleaned = cleanUp(uri);
            if (isCleaned) {
                System.out.println("Cleanup successful.");
            } else {
                System.err.println("Cleanup failed.");
            }
        }
    }


    /**
     * Clean Up - Removes the File from GCS
     */
    public boolean cleanUp(URI uri) {
        String bucketName = "audio-transcription-bucket-project"; // Hardcoded or from configuration
        String objectName = uri.getPath().substring(bucketName.length() + 2); // Remove leading '/' and bucket name
        System.out.println("Cleaning up file: " + objectName + " from bucket: " + bucketName);

        boolean deleted = storage.delete(bucketName, objectName);
        if (deleted) {
            System.out.println("File successfully deleted.");
        } else {
            System.err.println("Failed to delete file.");
        }

        return deleted;
    }


}
