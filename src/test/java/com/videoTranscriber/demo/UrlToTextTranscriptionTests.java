package com.videoTranscriber.demo;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UrlToTextTranscriptionTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileManipulationServices fileManipulationServices;

    @Autowired
    private ExtractAudioService processingService;

    @Autowired
    private AudioStorageService audioStorageService;

    @Test
    @WithMockUser
    void shouldDownloadAndTranscribeAudioFromURL() throws Exception {

        // URL for the tests:
        String testUrl = "https://www.youtube.com/watch?v=_pkWExJ_bIU";

        // Perform the request to /urlToText endpoint with the test URL and check for the response
        mockMvc.perform(post("/api/videos-upload/urlToText")
                        .param("url", testUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))  // CSRF token included
                .andExpect(status().isOk())  // Expect status 200 OK
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Transcribed Text:")));  // Ensure transcription is included in the response
    }

    @Test
    @WithMockUser
    void shouldReturnErrorForInvalidUrl() throws Exception {

        // Define an invalid URL for the test
        String invalidUrl = "invalid_url";

        // Perform the request to /urlToText endpoint with the invalid URL and check for the response
        mockMvc.perform(post("/api/videos-upload/urlToText")
                        .param("url", invalidUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))  // CSRF token included
                .andExpect(status().isInternalServerError())  // Expect status 500 for error
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error occurred:")));  // Ensure error message is included in the response
    }
}
