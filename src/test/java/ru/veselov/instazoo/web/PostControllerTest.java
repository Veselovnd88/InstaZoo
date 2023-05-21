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
import org.springframework.validation.BindingResult;
import ru.veselov.instazoo.dto.PostDTO;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.security.JwtProvider;
import ru.veselov.instazoo.service.PostService;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

import java.security.Principal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class PostControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    JwtProvider jwtProvider;

    @MockBean
    PostService postService;

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
    void shouldCallPostServiceToCreatePost() {
        PostDTO postDTO = TestUtils.getPostDTO();
        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/api/post/create").build())
                .headers(httpHeaders -> httpHeaders.add(Constants.AUTH_HEADER, header))
                .bodyValue(postDTO)
                .exchange().expectStatus().isCreated();

        Mockito.verify(postService, Mockito.times(1)).createPost(
                ArgumentMatchers.any(PostDTO.class),
                ArgumentMatchers.any(Principal.class));
        Mockito.verify(fieldErrorResponseService, Mockito.times(1))
                .validateFields(ArgumentMatchers.any(BindingResult.class));
    }

    @Test
    void shouldCallPostServiceToGeAllPosts() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/post/all").build())
                .headers(httpHeaders -> httpHeaders.add(Constants.AUTH_HEADER, header))
                .exchange().expectStatus().isOk();

        Mockito.verify(postService, Mockito.times(1)).getAllPosts();
    }

    @Test
    void shouldCallPostServiceToGetAllPostsForUser() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/post").build())
                .headers(httpHeaders -> httpHeaders.add(Constants.AUTH_HEADER, header))
                .exchange().expectStatus().isOk();

        Mockito.verify(postService, Mockito.times(1)).getAllPostsForUser(ArgumentMatchers.any(Principal.class));
    }

    @Test
    void shouldCallPostServiceToLike() {
        String postId = Constants.ANY_ID.toString();
        String username = Constants.USERNAME;
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/post/" + postId + "/" + username).path("/like").build())
                .headers(httpHeaders -> httpHeaders.add(Constants.AUTH_HEADER, header))
                .exchange().expectStatus().isAccepted();

        Mockito.verify(postService, Mockito.times(1)).likePost(Constants.ANY_ID, username);
    }

    @Test
    void shouldCallPostServiceToDeletePost() {
        String postId = Constants.ANY_ID.toString();
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/post/" + postId).path("/delete").build())
                .headers(httpHeaders -> httpHeaders.add(Constants.AUTH_HEADER, header))
                .exchange().expectStatus().isOk();

        Mockito.verify(postService, Mockito.times(1)).deletePost(
                ArgumentMatchers.any(Long.class), ArgumentMatchers.any(Principal.class));
    }

}