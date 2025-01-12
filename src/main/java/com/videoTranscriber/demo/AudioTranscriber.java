package com.videoTranscriber.demo;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
public class AudioTranscriber {

    private static final Logger logger = Logger.getLogger(AudioTranscriber.class.getName());

    // Define the maximum allowed file size (in bytes)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB limit

    public String transcribeAudio(String audioFilePath) throws Exception {
        // Ensure the file exists and is valid
        File audioFile = new File(audioFilePath);

        if (!audioFile.exists()) {
            throw new IOException("Audio file does not exist: " + audioFilePath);
        }

        // Check file size
        long fileSize = audioFile.length();
        if (fileSize > MAX_FILE_SIZE) { // If the file is too large
            return "The audio file is too large. Please provide a YouTube URL for transcription.";
        } else {
            // Check file extension
            String fileExtension = getFileExtension(audioFile);
            if (!isValidAudioFormat(fileExtension)) {
                throw new IllegalArgumentException("Invalid audio file format. Supported formats: WAV, MP3, FLAC");
            }
            // Process the short audio file
            return transcribeShortAudio(audioFile);
        }
    }

    private String transcribeShortAudio(File audioFile) {
        try (SpeechClient speechClient = SpeechClient.create()) {
            logger.info("Starting transcription for short audio file: " + audioFile.getName());

            // Convert audio file to ByteString
            ByteString audioBytes = ByteString.readFrom(new FileInputStream(audioFile));

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16) // Set the encoding type
                    .setSampleRateHertz(16000) // Set the sample rate
                    .setLanguageCode("en-US")  // Language code for transcription
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Perform synchronous transcription
            RecognizeResponse response = speechClient.recognize(config, audio);

            StringBuilder transcription = new StringBuilder();

            List<SpeechRecognitionResult> results = response.getResultsList();
            if (results.isEmpty()) {
                logger.warning("No transcription results found for file: " + audioFile.getName());
                return "No speech detected in the audio file.";
            }

            // Collect transcription results
            for (SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                transcription.append(alternative.getTranscript()).append("\n");
            }

            return transcription.toString();

        } catch (IOException e) {
            logger.severe("Failed to read the audio file: " + e.getMessage());
            return "Failed to read the audio file.";
        } catch (Exception e) {
            logger.severe("Error during transcription: " + e.getMessage());
            return "Error during transcription.";
        }
    }

    public String transcribeYouTubeAudio(String youtubeUrl) {
        // Extract audio from the YouTube URL and process it
        // This is where you'd typically use a library like yt-dlp or youtube-dl to download the audio
        // Assuming you have a method to handle this or manually extract it.

        try {
            // Simulating the process: In a real application, you'd download the audio from YouTube and send it to Google Cloud Speech API
            logger.info("Transcribing audio from YouTube URL: " + youtubeUrl);

            // Normally here you'd download and upload the audio, but since we're skipping Google Cloud Storage,
            // this method would just return a message saying the transcription has started.

            return "Started transcription for the audio from YouTube URL: " + youtubeUrl;
        } catch (Exception e) {
            logger.severe("Error during YouTube audio transcription: " + e.getMessage());
            return "Error during YouTube audio transcription.";
        }
    }

    // Helper method to get the file extension
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return ""; // No extension
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

    // Helper method to check if the audio file has a valid extension
    private boolean isValidAudioFormat(String fileExtension) {
        return fileExtension.equalsIgnoreCase("wav") || fileExtension.equalsIgnoreCase("mp3") || fileExtension.equalsIgnoreCase("flac");
    }
}

