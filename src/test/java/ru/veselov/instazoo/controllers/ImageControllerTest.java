package ru.veselov.instazoo.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.veselov.instazoo.model.ImageModel;
import ru.veselov.instazoo.service.ImageService;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

import java.time.format.DateTimeFormatter;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    MockMvc mockMvc;

    @Mock
    ImageService imageService;

    @InjectMocks
    ImageController imageController;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();
    }

    @Test
    void shouldCallImageServiceToUploadImageToUser() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1, 2, 3});
        MockMultipartHttpServletRequestBuilder file = MockMvcRequestBuilders
                .multipart(Constants.PREFIX_URL + "image/upload").file(multipartFile);

        mockMvc.perform(file)
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message").exists()).andExpect(
                        MockMvcResultMatchers.jsonPath("$.message").value(Matchers.containsString("Image"))
                );

        Mockito.verify(imageService, Mockito.times(1)).uploadImageToUser(
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
        );
    }

    @Test
    void shouldCallImageServiceToUploadImageToPost() throws Exception {
        String postId = Constants.ANY_ID.toString();
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1, 2, 3});
        MockMultipartHttpServletRequestBuilder file = MockMvcRequestBuilders
                .multipart(Constants.PREFIX_URL + "image/" + postId + "/upload").file(multipartFile);

        mockMvc.perform(file)
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(Matchers.containsString("Image"))
                );

        Mockito.verify(imageService, Mockito.times(1)).uploadImageToPost(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
        );
    }

    @Test
    void shouldCallImageServiceToGetAllProfileImage() throws Exception {
        ImageModel imageModel = TestUtils.getImageModel();
        Mockito.when(imageService.getImageToUser(ArgumentMatchers.any())).thenReturn(imageModel);

        mockMvc.perform(MockMvcRequestBuilders.get(Constants.PREFIX_URL + "/image/profile"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value(Matchers.containsString(imageModel.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(Matchers.hasToString(imageModel.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt")
                        .value(Matchers.containsString(
                                imageModel.getCreatedAt()
                                        .format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss")))));

        Mockito.verify(imageService, Mockito.times(1)).getImageToUser(ArgumentMatchers.any());
    }

    @Test
    void shouldCallImageServiceToGetPostImage() throws Exception {
        String postId = Constants.ANY_ID.toString();
        ImageModel imageModel = TestUtils.getImageModel();
        Mockito.when(imageService.getImageToPost(ArgumentMatchers.any())).thenReturn(imageModel);

        mockMvc.perform(MockMvcRequestBuilders.get(Constants.PREFIX_URL + "image/post/" + postId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value(Matchers.containsString(imageModel.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(Matchers.hasToString(imageModel.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt")
                        .value(Matchers.containsString(
                                imageModel.getCreatedAt()
                                        .format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss")))));


        Mockito.verify(imageService, Mockito.times(1)).getImageToPost(Constants.ANY_ID);
    }

}