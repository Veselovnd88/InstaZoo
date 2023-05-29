package ru.veselov.instazoo.web;

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
import ru.veselov.instazoo.dto.CommentDTO;
import ru.veselov.instazoo.model.Comment;
import ru.veselov.instazoo.service.CommentService;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    WebTestClient webTestClient;

    @Mock
    CommentService commentService;

    @Mock
    FieldErrorResponseService fieldErrorResponseService;

    @InjectMocks
    CommentController commentController;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(commentController).build();
    }

    @Test
    void shouldCallCommentServiceToCreateComment() {
        CommentDTO commentDTO = TestUtils.getCommentDTO();
        Comment comment = TestUtils.getComment();
        String postId = Constants.ANY_ID.toString();
        Mockito.when(commentService.saveComment(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
        )).thenReturn(comment);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/api/comment/" + postId).path("/create").build())
                .bodyValue(commentDTO)
                .exchange().expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.username").isEqualTo(comment.getUsername())
                .jsonPath("$.id").isEqualTo(comment.getId())
                .jsonPath("$.postId").isEqualTo(comment.getPostId())
                .jsonPath("$.userId").isEqualTo(comment.getUserId())
                .jsonPath("$.message").isEqualTo(comment.getMessage());

        Mockito.verify(commentService, Mockito.times(1)).saveComment(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
        );
        Mockito.verify(fieldErrorResponseService, Mockito.times(1)).validateFields(ArgumentMatchers.any());
    }

    @Test
    void shouldCallCommentServiceToGetAllCommentsToPost() {
        String postId = Constants.ANY_ID.toString();
        Mockito.when(commentService.getAllCommentsForPost(Constants.ANY_ID)).thenReturn(List.of(
                TestUtils.getComment(),
                TestUtils.getComment()
        ));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/comment/" + postId).build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$[0]").isEqualTo(TestUtils.getComment())
                .jsonPath("$[1]").isEqualTo(TestUtils.getComment())
                .jsonPath("$[2]").doesNotExist();


        Mockito.verify(commentService, Mockito.times(1)).getAllCommentsForPost(Constants.ANY_ID);
    }

    @Test
    void shouldCallCommentServiceToDeleteComment() {
        String commentId = Constants.ANY_ID.toString();

        webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/comment/" + commentId).path("/delete").build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").exists()
                .jsonPath("$.message").isEqualTo(String.format("Comment %s deleted", Constants.ANY_ID));

        Mockito.verify(commentService, Mockito.times(1)).deleteComment(Constants.ANY_ID);
    }

}
