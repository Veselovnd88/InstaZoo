package ru.veselov.instazoo.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.instazoo.dto.CommentDTO;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.security.JwtProvider;
import ru.veselov.instazoo.service.CommentService;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

import java.security.Principal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class CommentControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    JwtProvider jwtProvider;

    @MockBean
    CommentService commentService;

    @MockBean
    FieldErrorResponseService fieldErrorResponseService;

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
    void shouldCallCommentServiceToCreateComment() {
        CommentDTO commentDTO = TestUtils.getCommentDTO();
        String postId = Constants.ANY_ID.toString();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/api/comment/" + postId).path("/create").build())
                .headers(httpHeaders -> httpHeaders.add(Constants.AUTH_HEADER, header))
                .bodyValue(commentDTO)
                .exchange().expectStatus().isCreated();

        Mockito.verify(commentService, Mockito.times(1)).saveComment(
                ArgumentMatchers.any(Long.class),
                ArgumentMatchers.any(CommentDTO.class),
                ArgumentMatchers.any(Principal.class)
        );
        Mockito.verify(fieldErrorResponseService, Mockito.times(1)).validateFields(ArgumentMatchers.any());
    }

    @Test
    void shouldCallCommentServiceToGetAllCommentsToPost() {
        String postId = Constants.ANY_ID.toString();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/comment/" + postId).build())
                .headers(httpHeaders -> httpHeaders.add(Constants.AUTH_HEADER, header))
                .exchange().expectStatus().isOk();

        Mockito.verify(commentService, Mockito.times(1)).getAllCommentsForPost(Constants.ANY_ID);
    }

    @Test
    void shouldCallCommentServiceToDeleteComment() {
        String commentId = Constants.ANY_ID.toString();

        webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/comment/" + commentId).path("/delete").build())
                .headers(httpHeaders -> httpHeaders.add(Constants.AUTH_HEADER, header))
                .exchange().expectStatus().isOk();

        Mockito.verify(commentService, Mockito.times(1)).deleteComment(Constants.ANY_ID);
    }

}