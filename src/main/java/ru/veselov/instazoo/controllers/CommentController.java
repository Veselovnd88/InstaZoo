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
import ru.veselov.instazoo.dto.CommentDTO;
import ru.veselov.instazoo.model.Comment;
import ru.veselov.instazoo.payload.response.ResponseMessage;
import ru.veselov.instazoo.service.CommentService;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Comment controller", description = "API for managing comments")
public class CommentController {

    private final CommentService commentService;

    private final FieldErrorResponseService fieldErrorResponseService;

    @Operation(summary = "Create comment", description = "Returns saved comment")
    @ApiResponse(responseCode = "200", description = "Successfully created",
            content = @Content(schema = @Schema(implementation = Comment.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @PostMapping("/{postId}/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = CommentDTO.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            ))
                                 @Valid @RequestBody CommentDTO commentDTO,
                                 BindingResult bindingResult,
                                 @Parameter(in = ParameterIn.PATH, description = "Post Id")
                                 @PathVariable("postId") String postId,
                                 Principal principal) {
        fieldErrorResponseService.validateFields(bindingResult);
        return commentService.saveComment(Long.parseLong(postId), commentDTO, principal);
    }

    @Operation(summary = "Get all comments to post", description = "Returns array of comments")
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDTO.class)),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping("/{postId}")
    public List<Comment> getAllCommentsToPost(@PathVariable("postId") String postId) {
        return commentService.getAllCommentsForPost(Long.parseLong(postId));
    }

    @Operation(summary = "Deleting comment", description = "Delete comment and return info message")
    @ApiResponse(responseCode = "200", description = "Successfully deleted",
            content = @Content(schema = @Schema(implementation = ResponseMessage.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
    @DeleteMapping("/{commentId}/delete")
    public ResponseMessage deleteComment(@PathVariable("commentId") String commentId) {
        commentService.deleteComment(Long.parseLong(commentId));
        return new ResponseMessage(String.format("Comment %s deleted", commentId));
    }

}
