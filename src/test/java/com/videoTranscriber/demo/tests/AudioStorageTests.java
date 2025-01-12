package com.videoTranscriber.demo.tests;

import com.videoTranscriber.demo.AudioStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class AudioStorageTests {

    private AudioStorageService audioStorageService;

    @BeforeEach
    void setUp() {
        audioStorageService = new AudioStorageService();

    }

    @Test
    void testUploadAudio_FromLocalFile() throws IOException {
        // Path to the local WAV file
        String filePath = "src/test/resources/sample.wav";

        File localFile = new File(filePath);
        assertTrue(localFile.exists(), "Test file does not exist");

        // Read the local file and convert it to MultipartFile
        FileInputStream inputStream = new FileInputStream(localFile);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                localFile.getName(),
                "audio/wav",
                inputStream
        );

        // Call the service to upload the file
        URI uri = audioStorageService.uploadAudio(multipartFile, "audio-transcription-bucket-project");
        assertNotNull(uri);
        assertTrue(uri.toString().contains("https://storage.googleapis.com/audio-transcription-bucket-project/" + localFile.getName()));

        inputStream.close();
    }

    @Test
    void testUploadAudio_InvalidFileFormat() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "dummy content".getBytes());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                audioStorageService.uploadAudio(file, "test-bucket")
        );
        assertEquals("Invalid file. Please upload a valid WAV audio file.", exception.getMessage());
    }

    @Test
    void testUploadAudio_EmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "", "audio/wav", new byte[0]);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                audioStorageService.uploadAudio(file, "test-bucket")
        );
        assertEquals("Invalid file. Please upload a valid WAV audio file.", exception.getMessage());
    }
}
