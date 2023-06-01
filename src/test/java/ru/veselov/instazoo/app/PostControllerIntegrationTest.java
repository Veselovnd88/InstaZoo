package ru.veselov.instazoo.app;

import org.hamcrest.Matchers;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
@DirtiesContext
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
        UserEntity saved = userRepository.save(userEntity);
        user.setId(saved.getId());
        upAuthToken = new UsernamePasswordAuthenticationToken(user, null);
        jwtHeader = Constants.BEARER_PREFIX + jwtGenerator.generateToken(upAuthToken);
    }

    @AfterEach
    void deleteAll() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreatePostAndReturn() {
        PostDTO postDTO = Instancio.of(PostDTO.class)
                .ignore(Select.field(PostDTO::getId))
                .create();

        WebTestClient.ResponseSpec created = webTestClient.post().uri(uriBuilder ->
                        uriBuilder.path(Constants.PREFIX_URL + "post").path("/create").build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .bodyValue(postDTO)
                .exchange().expectStatus().isCreated();
        checkBody(created, postDTO);
    }

    @Test
    void shouldReturnAllPosts() {
        PostEntity postEntity = Instancio.of(PostEntity.class)
                .ignore(Select.field(PostEntity::getUser))
                .ignore(Select.field(PostEntity::getId)).create();
        PostEntity postEntity2 = Instancio.of(PostEntity.class)
                .ignore(Select.field(PostEntity::getUser))
                .ignore(Select.field(PostEntity::getId)).create();
        postRepository.save(postEntity);
        postRepository.save(postEntity2);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "post").path("/all").build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(2)
                .jsonPath("$[0].caption").value(Matchers.anyOf(
                        Matchers.containsString(postEntity2.getCaption()),
                        Matchers.containsString(postEntity.getCaption())))
                .jsonPath("$[1].caption").value(Matchers.anyOf(
                        Matchers.containsString(postEntity2.getCaption()),
                        Matchers.containsString(postEntity.getCaption())))
                .jsonPath("$[0].username").value(Matchers.anyOf(
                        Matchers.containsString(postEntity2.getUsername()),
                        Matchers.containsString(postEntity.getUsername())))
                .jsonPath("$[1].username").value(Matchers.anyOf(
                        Matchers.containsString(postEntity2.getUsername()),
                        Matchers.containsString(postEntity.getUsername())))
                .jsonPath("$[2]").doesNotExist();
    }

    @Test
    void shouldReturnAllPostsOfCurrentUser() {
        PostEntity userPostEntity = Instancio.of(PostEntity.class)
                .ignore(Select.field(PostEntity::getUser))
                .ignore(Select.field(PostEntity::getId)).create();
        PostEntity notUserPostEntity = Instancio.of(PostEntity.class)
                .ignore(Select.field(PostEntity::getUser))
                .ignore(Select.field(PostEntity::getId)).create();
        UserEntity userEntity = TestUtils.getUserEntity();
        userEntity.setId(user.getId());
        userPostEntity.setUser(userEntity);
        postRepository.save(userPostEntity);
        postRepository.save(notUserPostEntity);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "post").build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(1)
                .jsonPath("$[0].caption").isEqualTo(userPostEntity.getCaption())
                .jsonPath("$[0].username").isEqualTo(userPostEntity.getUsername())
                .jsonPath("$[1]").doesNotExist();
    }

    @Test
    void shouldLikeAndDislikePost() {
        String username = user.getUsername();
        PostEntity userPostEntity = Instancio.of(PostEntity.class)
                .ignore(Select.field(PostEntity::getUser))
                .ignore(Select.field(PostEntity::getId)).create();
        PostEntity save = postRepository.save(userPostEntity);
        Long postId = save.getId();
        //user like post
        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "post")
                        .path("/" + postId.toString())
                        .path("/" + username)
                        .path("/like").build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isAccepted()
                .expectBody().jsonPath("$").exists()
                .jsonPath("$.likedUsers").isArray()
                .jsonPath("$.likedUsers").value(Matchers.hasItem(username))
                .jsonPath("$.likes").isEqualTo(userPostEntity.getLikes() + 1);
        //user dislike post
        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "post")
                        .path("/" + postId.toString())
                        .path("/" + username)
                        .path("/like").build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isAccepted()
                .expectBody().jsonPath("$").exists()
                .jsonPath("$.likedUsers").isArray()
                .jsonPath("$.likes").isEqualTo(userPostEntity.getLikes());
    }

    @Test
    void shouldDeletePost() {
        PostEntity userPostEntity = Instancio.of(PostEntity.class)
                .ignore(Select.field(PostEntity::getUser))
                .ignore(Select.field(PostEntity::getId)).create();
        UserEntity userEntity = TestUtils.getUserEntity();
        userEntity.setId(user.getId());
        userPostEntity.setUser(userEntity);
        PostEntity saved = postRepository.save(userPostEntity);
        Long postId = saved.getId();

        webTestClient.delete().uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "post")
                        .path("/" + postId.toString()).path("/delete").build())
                .header(Constants.AUTH_HEADER, jwtHeader).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.message").isEqualTo(String.format("Post %s deleted", postId));
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
