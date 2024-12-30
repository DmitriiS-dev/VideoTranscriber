package com.videoTranscriber.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VideoTranscriberApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser
	void shouldUploadVideosandReturnOk() throws Exception{
		MockMultipartFile videoFile = new MockMultipartFile(
				"file",
				"test-video.mp4",
				"video/mp4",
				"dummy video content".getBytes()
		);

		mockMvc.perform(multipart("/api/videos-upload")
				.file(videoFile)
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk());

	}

}
