package ru.veselov.instazoo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.instazoo.dto.PostDTO;
import ru.veselov.instazoo.model.Post;
import ru.veselov.instazoo.payload.response.ResponseMessage;
import ru.veselov.instazoo.service.PostService;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Post controller", description = "API for managing posts")
public class PostController {

    private final PostService postService;

    private final FieldErrorResponseService fieldErrorResponseService;

    @Operation(summary = "Create post", description = "Create and return post")
    @ApiResponse(responseCode = "201", description = "Successfully created", content =
    @Content(schema = @Schema(implementation = Post.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@io.swagger.v3.oas.annotations.parameters.RequestBody(content =
    @Content(schema = @Schema(implementation = PostDTO.class),
            mediaType = MediaType.APPLICATION_JSON_VALUE
    ))
                           @Valid @RequestBody PostDTO postDTO, BindingResult bindingResult, Principal principal) {
        fieldErrorResponseService.validateFields(bindingResult);
        return postService.createPost(postDTO, principal);
    }

    @Operation(summary = "Get all posts of all users", description = "Return array of posts")
    @ApiResponse(responseCode = "200", description = "Success", content =
    @Content(array = @ArraySchema(schema = @Schema(implementation = Post.class)), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping("/all")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @Operation(summary = "Get all posts of current user", description = "Return array of posts")
    @ApiResponse(responseCode = "200", description = "Success", content =
    @Content(array = @ArraySchema(schema = @Schema(implementation = Post.class)), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping
    public List<Post> getAllPostsForUser(Principal principal) {
        return postService.getAllPostsForUser(principal);
    }

    @Operation(summary = "Like or dislike post", description = "Return updated post info")
    @ApiResponse(responseCode = "200", description = "Success", content =
    @Content(schema = @Schema(implementation = Post.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @PostMapping("/{postId}/{username}/like")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Post likePost(@Parameter(in = ParameterIn.PATH, description = "Post id")
                         @PathVariable("postId") String postId,
                         @Parameter(in = ParameterIn.PATH, description = "User name")
                         @PathVariable("username") String username) {
        return postService.likePost(Long.parseLong(postId), username);
    }

    @Operation(summary = "Delete post", description = "Delete post, returns info message")
    @ApiResponse(responseCode = "200", description = "Successfully deleted", content =
    @Content(schema = @Schema(implementation = ResponseMessage.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @DeleteMapping("/{postId}/delete")
    public ResponseMessage deletePost(@Parameter(in = ParameterIn.PATH, description = "Post id to delete")
                                      @PathVariable("postId") String postId, Principal principal) {
        postService.deletePost(Long.parseLong(postId), principal);
        return new ResponseMessage(String.format("Post %s deleted", postId));
    }

}
