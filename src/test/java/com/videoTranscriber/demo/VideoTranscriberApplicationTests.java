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

	private final Path testVideoPath = Paths.get("src/test/resources/test-video.mp4");
	private final Path uploadDir = Paths.get("uploads");

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

		mockMvc.perform(multipart("/api/videos-upload")
						.file(videoFile)
						.with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk());
	}
}
