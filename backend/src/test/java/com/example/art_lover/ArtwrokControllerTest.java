package com.example.art_lover;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.example.art_lover.service.*;
import com.example.art_lover.controller.*;
import com.example.art_lover.dto.artwork.ArtworkSearchHit;
import com.example.art_lover.dto.artwork.ImageBoxBounds;
import com.example.art_lover.model.*;;

@WebMvcTest(ArtLoverController.class)
public class ArtwrokControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ArtworkService artworkService;

        @MockitoBean
        private GeminiAIDescriptionService descriptionService;

        @MockitoBean
        private GeminiAIImageRecognitionService imageRecognitionService;

        @MockitoBean
        private R2ImageService r2ImageService;

        @MockitoBean
        JWTService jwtService;

        @Test
        void SaveNewArtworkReturnsOk() throws Exception {

                Authentication authentication = mock(Authentication.class);
                when(authentication.getName()).thenReturn("1234567");

                MockMultipartFile imageFile = new MockMultipartFile(
                                "imageFile",
                                "test.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "dummy-image".getBytes());

                mockMvc.perform(multipart("/api/save")
                                .principal(authentication)
                                .file(imageFile)
                                .param("artist", "Leonardo da Vinci")
                                .param("artistId", "1234567890")
                                .param("title", "Mona Lisa")
                                .param("year", "1500")
                                .param("description", "Mona Lisa Description")
                                .param("movement", "Renaissance")
                                .param("bookmark", "true")
                                .param("imageUrl", "https:example.com")
                                .param("imageKey", "https:example.com")
                                .param("previewKey", "https://example.com")
                                .param("box", "x: .8, y: .2, width: .8, height: .8"))
                                .andExpect(status().isOk());
        }

        @Test
        void DeleteArtworkReturnsOk() throws Exception {

                Authentication authentication = mock(Authentication.class);
                when(authentication.getName()).thenReturn("1234567");

                mockMvc.perform(delete("/api/delete/{id}", "1234567")
                                .principal(authentication))
                                .andExpect(status().isOk());

        }

        @Test
        void UpdateArtworkReturnsOk() throws Exception {

                Authentication authentication = mock(Authentication.class);
                when(authentication.getName()).thenReturn("1234567");

                MockMultipartFile imageFile = new MockMultipartFile(
                                "imageFile",
                                "test.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "dummy-image".getBytes());

                mockMvc.perform(multipart("/api/update/{id}", "1234567")
                                .principal(authentication)
                                .file(imageFile)
                                .param("artistId", "1234567890")
                                .param("title", "Mona Lisa")
                                .param("year", "1500")
                                .param("description", "Mona Lisa Description")
                                .param("movement", "Renaissance")
                                .param("bookmark", "true")
                                .param("continent", "Europe")
                                .param("country", "Italy")
                                .param("imageUrl", "https:example.com")
                                .param("imageKey",
                                                "https:example.com")
                                .with(request -> {
                                        request.setMethod("PATCH");
                                        return request;
                                }))
                                .andExpect(status().isOk());
        }

        @Test
        void BookmarkUpdatedReturnsOk() throws Exception {

                Authentication authentication = mock(Authentication.class);
                when(authentication.getName()).thenReturn("1234567");

                mockMvc.perform(patch("/api/update/bookmark/{id}",
                                "1234567")
                                .principal(authentication)
                                .param("bookmark", "true")
                                .param("userId", "1234567"))
                                .andExpect(status().isOk());
        }

        @Test
        void GeminiGenerateDescriptionReturnsOk() throws Exception {
                Authentication authentication = mock(Authentication.class);
                when(authentication.getName()).thenReturn("1234567");

                when(descriptionService.generateDescription("This is a user prompt."))
                                .thenReturn("Generated description");

                mockMvc.perform(get("/api/generate-description")
                                .principal(authentication)
                                .param("userPrompt", "This is a user prompt."))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Generated description"));
        }

        @Test
        void GeminiRecognizeImageRuturnsOk() throws Exception {

                Authentication authentication = mock(Authentication.class);
                when(authentication.getName()).thenReturn("1234567");

                MockMultipartFile image = new MockMultipartFile(
                                "image",
                                "test.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "dummy-image".getBytes());

                ArtworkSearchHit hit = new ArtworkSearchHit(
                                "Mona Lisa",
                                "Leonardo",
                                "1500",
                                "Medieval",
                                List.of("https://imageurl.com"),
                                "Description",
                                "mona lisa leonardo",
                                true,
                                new ImageBoxBounds(0.2, 0.3, 0.7, 0.7));

                when(imageRecognitionService.recognizeImage(any()))
                                .thenReturn(List.of(hit));

                mockMvc.perform(multipart("/api/recognize")
                                .principal(authentication)
                                .file(image)).andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray());
        }

        @Test
        void FetchArtistsReturnsOk() throws Exception {
                Authentication authentication = mock(Authentication.class);
                when(authentication.getName()).thenReturn("1234567");

                ArtistModel artist = new ArtistModel();
                artist.setName("Leonardo da Vinci");

                when(artworkService.findAllArtists()).thenReturn(List.of(artist));

                mockMvc.perform(get("/api/artists")
                                .principal(authentication))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray());
        }

}
