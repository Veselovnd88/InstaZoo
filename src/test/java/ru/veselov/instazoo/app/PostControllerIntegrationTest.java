package ru.veselov.instazoo.app;

import org.hamcrest.Matchers;
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
import ru.veselov.instazoo.entity.PostEntity;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.repository.PostRepository;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.security.jwt.JwtGenerator;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class PostControllerIntegrationTest extends PostgresTestContainersConfig {

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

    @Test
    void shouldReturnAllPosts() {
        PostEntity postEntity = TestUtils.getPostEntity();
        PostEntity postEntity2 = TestUtils.getPostEntity();
        postEntity2.setCaption("AnotherCaption");
        postEntity2.setUsername("AnotherUser");
        postEntity2.setId(null);
        postRepository.save(postEntity);
        postRepository.save(postEntity2);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "/post").path("/all").build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$[0].caption").value(Matchers.anyOf(
                        Matchers.containsString(postEntity2.getCaption()),
                        Matchers.containsString(postEntity.getCaption())))
                .jsonPath("$.size()").isEqualTo(2)
                .jsonPath("$[0].username").value(Matchers.anyOf(
                        Matchers.containsString(postEntity2.getUsername()),
                        Matchers.containsString(postEntity.getUsername())))
                .jsonPath("$[2]").doesNotExist();//TODO check second element
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
