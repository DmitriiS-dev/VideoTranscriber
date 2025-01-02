package com.videoTranscriber.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VideoTranscriberApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FileService fileService;

	@Autowired
	private ProcessingService processingService;

	private final Path testVideoPath = Paths.get("src/test/resources/test-video.mp4");
	private final Path uploadDir = Paths.get("uploads");
	private final Path audioOutputDir = Paths.get("uploads/audio");

	@Test
	@WithMockUser
	void shouldUploadVideosAndReturnOk() throws Exception {
		// Ensure test video exists
		assertTrue(Files.exists(testVideoPath), "Test video file is missing");

		// Read the test video file as bytes
		byte[] videoBytes = Files.readAllBytes(testVideoPath);

		MockMultipartFile videoFile = new MockMultipartFile(
				"file",
				"test-video.mp4",
				"video/mp4",
				videoBytes
		);

		// Perform the upload request and check that it's OK
		mockMvc.perform(multipart("/api/videos-upload")
						.file(videoFile)
						.with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk());

		// Simulate the video file being saved and audio extraction
		Path savedFilePath = fileService.saveFile(videoFile, "uploads");

		// Ensure audio extraction happens successfully
		String audioFilePath = processingService.extractAudio(savedFilePath.toString(), "uploads/audio");

		// Verify the audio file exists
		Path extractedAudioFile = Paths.get(audioFilePath);
		assertTrue(Files.exists(extractedAudioFile), "Audio file was not created.");

		// Optional: Check if the audio file is not empty
		assertTrue(Files.size(extractedAudioFile) > 0, "Audio file is empty.");

		// Cleanup: Delete the files after the test
		Files.delete(extractedAudioFile);
		Files.delete(savedFilePath);
	}
}
