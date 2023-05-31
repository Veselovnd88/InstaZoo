package ru.veselov.instazoo.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.instazoo.app.testcontainers.PostgresTestContainersConfig;
import ru.veselov.instazoo.dto.PostDTO;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.repository.PostRepository;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.security.jwt.JwtGenerator;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PostControllerIntegrationTest extends PostgresTestContainersConfig {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    JwtGenerator jwtGenerator;

    User user;

    UsernamePasswordAuthenticationToken upAuthToken;

    String jwtHeader;

    @BeforeEach
    void init() {
        user = TestUtils.getUser();
        UserEntity userEntity = TestUtils.getUserEntity();
        UserEntity save = userRepository.save(userEntity);
        user.setId(save.getId());
        upAuthToken = new UsernamePasswordAuthenticationToken(user, null);
        jwtHeader = Constants.BEARER_PREFIX + jwtGenerator.generateToken(upAuthToken);
    }

    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void shouldCreatePost() {
        PostDTO postDTO = TestUtils.getPostDTO();

        WebTestClient.ResponseSpec created = webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "post").path("/create").build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .bodyValue(postDTO)
                .exchange().expectStatus().isCreated();
        checkBody(created, postDTO);
    }

    private void checkBody(WebTestClient.ResponseSpec spec, PostDTO post) {
        spec.expectBody().jsonPath("$").exists()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.username").isEqualTo(user.getUsername())
                .jsonPath("$.caption").isEqualTo(post.getCaption())
                .jsonPath("$.location").isEqualTo(post.getLocation())
                .jsonPath("$.createdAt").isNotEmpty();
    }

}
