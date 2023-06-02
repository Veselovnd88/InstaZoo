package ru.veselov.instazoo.app;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.veselov.instazoo.app.testcontainers.PostgresTestContainersConfig;
import ru.veselov.instazoo.entity.PostEntity;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.repository.ImageRepository;
import ru.veselov.instazoo.repository.PostRepository;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.security.jwt.JwtGenerator;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class ImageControllerIntegrationTest extends PostgresTestContainersConfig {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    JwtGenerator jwtGenerator;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    ImageRepository imageRepository;

    User user;

    String jwtHeader;


    @BeforeEach
    void init() {
        user = TestUtils.getUser();
        UserEntity userEntity = TestUtils.getUserEntity();
        UserEntity saved = userRepository.save(userEntity);
        user.setId(saved.getId());
        UsernamePasswordAuthenticationToken upAuthToken = new UsernamePasswordAuthenticationToken(user, null);
        jwtHeader = Constants.BEARER_PREFIX + jwtGenerator.generateToken(upAuthToken);
    }

    @AfterEach
    void clear() {
        userRepository.deleteAll();
        postRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    void shouldUploadImageToUser() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new byte[]{1, 2, 3}).filename("file");

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "image")
                        .path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isAccepted()
                .expectBody().jsonPath("$.message").isEqualTo("Image upload successfully");
    }

    @Test
    void shouldUploadImageToPost() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new byte[]{1, 2, 3}).filename("file");
        PostEntity userPostEntity = Instancio.of(PostEntity.class)
                .ignore(Select.field(PostEntity::getUser))
                .ignore(Select.field(PostEntity::getId)).create();
        UserEntity userEntity = TestUtils.getUserEntity();
        userEntity.setId(user.getId());
        userPostEntity.setUser(userEntity);
        PostEntity saved = postRepository.save(userPostEntity);
        Long postId = saved.getId();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "image")
                        .path("/" + postId.toString())
                        .path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isAccepted()
                .expectBody().jsonPath("$.message").isEqualTo("Image upload successfully");
    }

    @Test
    void shouldReturnProfileImage() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new byte[]{1, 2, 3}).filename("file");
        //send image to set up user profile
        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "image")
                        .path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isAccepted();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "image")
                        .path("/profile").build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo("file");
    }

    @Test
    void shouldReturnImageToPost() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new byte[]{1, 2, 3}).filename("file");
        PostEntity userPostEntity = Instancio.of(PostEntity.class)
                .ignore(Select.field(PostEntity::getUser))
                .ignore(Select.field(PostEntity::getId)).create();
        UserEntity userEntity = TestUtils.getUserEntity();
        userEntity.setId(user.getId());
        userPostEntity.setUser(userEntity);
        PostEntity saved = postRepository.save(userPostEntity);
        Long postId = saved.getId();
        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "image")
                        .path("/" + postId.toString())
                        .path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isAccepted();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "image")
                        .path("/post")
                        .path("/" + postId.toString()).build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo("file");
    }


}
