package ru.veselov.instazoo.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.security.JwtProvider;
import ru.veselov.instazoo.service.ImageService;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ImageControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    JwtProvider jwtProvider;

    @MockBean
    ImageService imageService;

    User user;

    String header;

    @BeforeEach
    void init() {
        user = TestUtils.getUser();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, "Pass");
        String jwt = jwtProvider.generateToken(token);
        header = "Bearer " + jwt;
    }

    @Test
    void shouldCallImageServiceToUploadImageToUser() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new byte[]{1, 2, 3}, MediaType.MULTIPART_FORM_DATA);

        webTestClient.post().uri(
                        uriBuilder -> uriBuilder.path("/api/image/upload").build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .headers(httpHeaders -> httpHeaders.add(Constants.AUTH_HEADER, header))
                .bodyValue(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().isAccepted();

        Mockito.verify(imageService, Mockito.times(1)).uploadImageToUser(
                ArgumentMatchers.any(MultipartFile.class),
                ArgumentMatchers.any(Principal.class)
        );
    }

    @Test
    void uploadImageToPost() {
    }
}