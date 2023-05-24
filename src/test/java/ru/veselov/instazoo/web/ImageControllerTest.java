package ru.veselov.instazoo.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.security.JwtProvider;
import ru.veselov.instazoo.service.ImageService;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

import java.security.Principal;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ImageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtProvider jwtProvider;

    @MockBean
    ImageService imageService;

    @MockBean
    UserRepository userRepository;

    User user;

    String header;

    @BeforeEach
    void init() {
        user = TestUtils.getUser();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, "Pass");
        String jwt = jwtProvider.generateToken(token);
        header = "Bearer " + jwt;
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(TestUtils.getUserEntity()));
    }

    @Test
    void shouldCallImageServiceToUploadImageToUser() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1, 2, 3});
        MockMultipartHttpServletRequestBuilder file = MockMvcRequestBuilders
                .multipart("/api/image/upload").file(multipartFile);

        mockMvc.perform(file.header(Constants.AUTH_HEADER, header))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        Mockito.verify(imageService, Mockito.times(1)).uploadImageToUser(
                ArgumentMatchers.any(MultipartFile.class),
                ArgumentMatchers.any(Principal.class)
        );
    }

    @Test
    void shouldCallImageServiceToUploadImageToPost() throws Exception {
        String postId = Constants.ANY_ID.toString();
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1, 2, 3});
        MockMultipartHttpServletRequestBuilder file = MockMvcRequestBuilders
                .multipart("/api/image/" + postId + "/upload").file(multipartFile);

        mockMvc.perform(file.header(Constants.AUTH_HEADER, header))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        Mockito.verify(imageService, Mockito.times(1)).uploadImageToPost(
                ArgumentMatchers.any(MultipartFile.class),
                ArgumentMatchers.any(Principal.class),
                ArgumentMatchers.any(Long.class)
        );
    }

    @Test
    void shouldCallImageServiceToGetAllProfileImage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/image/profile")
                        .header(Constants.AUTH_HEADER, header))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(imageService, Mockito.times(1)).getImageToUser(ArgumentMatchers.any(Principal.class));
    }

    @Test
    void shouldCallImageServiceToGetPostImage() throws Exception {
        String postId = Constants.ANY_ID.toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/image/post/" + postId)
                        .header(Constants.AUTH_HEADER, header))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(imageService, Mockito.times(1)).getImageToPost(Constants.ANY_ID);
    }

}