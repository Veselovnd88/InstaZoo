package ru.veselov.instazoo.web;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.instazoo.dto.PostDTO;
import ru.veselov.instazoo.model.Post;
import ru.veselov.instazoo.service.PostService;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    WebTestClient webTestClient;

    @Mock
    PostService postService;

    @Mock
    FieldErrorResponseService fieldErrorResponseService;

    @InjectMocks
    PostController postController;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(postController).build();
    }

    @Test
    void shouldCreateAndReturnPost() {
        PostDTO postDTO = TestUtils.getPostDTO();
        Post post = TestUtils.getPost();
        Mockito.when(postService.createPost(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(post);
        WebTestClient.ResponseSpec created = webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "post/create").build())
                .bodyValue(postDTO)
                .exchange().expectStatus().isCreated();

        checkBody(created, post);

        Mockito.verify(postService, Mockito.times(1)).createPost(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(fieldErrorResponseService, Mockito.times(1)).validateFields(ArgumentMatchers.any());
    }

    @Test
    void shouldReturnAllPosts() {
        Post post = TestUtils.getPost();
        Mockito.when(postService.getAllPosts()).thenReturn(List.of(
                post,
                post
        ));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "post/all").build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$[0]").exists()
                .jsonPath("$[1]").exists()
                .jsonPath("$[2]").doesNotExist()
                .jsonPath("$[0]");

        Mockito.verify(postService, Mockito.times(1)).getAllPosts();
    }

    @Test
    void shouldReturnAllPostsForUser() {
        Post post = TestUtils.getPost();
        Mockito.when(postService.getAllPostsForUser(ArgumentMatchers.any())).thenReturn(List.of(
                post,
                post
        ));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "post").build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$[0]").exists()
                .jsonPath("$[1]").exists()
                .jsonPath("$[2]").doesNotExist();

        Mockito.verify(postService, Mockito.times(1)).getAllPostsForUser(ArgumentMatchers.any());
    }

    @Test
    void shouldLikePostAndReturnPost() {
        String postId = Constants.ANY_ID.toString();
        String username = Constants.USERNAME;
        Post post = TestUtils.getPost();
        Mockito.when(postService.likePost(Constants.ANY_ID, username)).thenReturn(post);

        WebTestClient.ResponseSpec accepted = webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "post/" + postId + "/" + username)
                        .path("/like").build())
                .exchange().expectStatus().isAccepted();

        checkBody(accepted, post);
        Mockito.verify(postService, Mockito.times(1)).likePost(Constants.ANY_ID, username);
    }

    @Test
    void shouldDeletePostAndReturnResponseMessage() {
        String postId = Constants.ANY_ID.toString();

        webTestClient.delete().uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "post/" + postId)
                        .path("/delete").build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").exists()
                .jsonPath("$.message").isEqualTo(String.format("Post %s deleted", postId));

        Mockito.verify(postService, Mockito.times(1)).deletePost(
                ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    private void checkBody(WebTestClient.ResponseSpec spec, Post post) {
        spec.expectBody().jsonPath("$").exists()
                .jsonPath("$.id").isEqualTo(post.getId())
                .jsonPath("$.username").isEqualTo(post.getUsername())
                .jsonPath("$.caption").isEqualTo(post.getCaption())
                .jsonPath("$.location").isEqualTo(post.getLocation())
                .jsonPath("$.createdAt").isEqualTo(
                        post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss")))
                .jsonPath("$.likedUsers").isArray()
                .jsonPath("$.likedUsers[0]").value(Matchers.containsString("dog"))
                .jsonPath("$.likedUsers[1]").value(Matchers.containsString("dog"))
                .jsonPath("$.likedUsers[2]").doesNotExist();
    }

}