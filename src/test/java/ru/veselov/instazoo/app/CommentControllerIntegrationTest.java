package ru.veselov.instazoo.app;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.instazoo.dto.CommentDTO;
import ru.veselov.instazoo.entity.CommentEntity;
import ru.veselov.instazoo.entity.PostEntity;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.repository.CommentRepository;
import ru.veselov.instazoo.repository.PostRepository;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.security.jwt.JwtGenerator;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CommentControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

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
    void shouldCreateCommentAndReturn() {
        CommentDTO commentDTO = Instancio.of(CommentDTO.class).create();
        PostEntity postEntity = Instancio.of(PostEntity.class)
                .ignore(Select.field(PostEntity::getUser))
                .ignore(Select.field(PostEntity::getId)).create();
        PostEntity saved = postRepository.save(postEntity);
        Long postId = saved.getId();

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "comment")
                        .path("/" + postId.toString()).path("/create")
                        .build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .bodyValue(commentDTO)
                .exchange().expectStatus().isCreated()
                .expectBody().jsonPath("$.message").isEqualTo(commentDTO.getMessage())
                .jsonPath("$.username").isEqualTo(user.getUsername())
                .jsonPath("$.id").exists()
                .jsonPath("$.userId").isEqualTo(user.getId())
                .jsonPath("$.postId").isEqualTo(postId.toString());
    }

    @Test
    void shouldReturnAllCommentsToPost() {
        PostEntity postEntity = Instancio.of(PostEntity.class)
                .ignore(Select.field(PostEntity::getUser))
                .ignore(Select.field(PostEntity::getId)).create();
        PostEntity saved = postRepository.save(postEntity);
        Long postId = saved.getId();
        for (int i = 0; i < 5; i++) {
            CommentEntity commentEntity = Instancio.of(CommentEntity.class)
                    .ignore(Select.field(CommentEntity::getId))
                    .set(Select.field(CommentEntity::getUsername), user.getUsername())
                    .set(Select.field(CommentEntity.class, "post"), saved)
                    .create();
            commentRepository.save(commentEntity);
        }

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "comment")
                        .path("/" + postId.toString())
                        .build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray().jsonPath("$.size()").isEqualTo(5)
                .jsonPath("$[0].username").isEqualTo(user.getUsername())
                .jsonPath("$[0].postId").isEqualTo(saved.getId());
    }

    @Test
    void shouldDeletePost() {
        PostEntity postEntity = Instancio.of(PostEntity.class)
                .ignore(Select.field(PostEntity::getUser))
                .ignore(Select.field(PostEntity::getId)).create();
        PostEntity saved = postRepository.save(postEntity);
        Long postId = saved.getId();
        CommentEntity commentEntity = Instancio.of(CommentEntity.class)
                .ignore(Select.field(CommentEntity::getId))
                .set(Select.field(CommentEntity::getUsername), user.getUsername())
                .set(Select.field(CommentEntity.class, "post"), saved)
                .create();
        CommentEntity willDeleteThisComment = commentRepository.save(commentEntity);
        Long commentId = willDeleteThisComment.getId();

        webTestClient.delete().uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "comment")
                        .path("/" + commentId.toString())
                        .path("/delete")
                        .build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").exists()
                .jsonPath("$.message").isEqualTo(String.format("Comment %s deleted", commentId));
        //Check that no comments for this post
        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "comment")
                        .path("/" + postId.toString())
                        .build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isOk();
    }


}
