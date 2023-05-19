package ru.veselov.instazoo.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import ru.veselov.instazoo.service.CommentService;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final FieldErrorResponseService fieldErrorResponseService;

    @PostMapping("/{postId}/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@Valid @RequestBody CommentDTO commentDTO,
                                 BindingResult bindingResult,
                                 @PathVariable("postId") String postId,
                                 Principal principal) {
        fieldErrorResponseService.validateFields(bindingResult);

        return commentService.saveComment(Long.parseLong(postId), commentDTO, principal);
    }

    @GetMapping("/{postId}")
    public List<Comment> getAllCommentsToPost(@PathVariable("postId") String postId) {
        return commentService.getAllCommentsForPost(Long.parseLong(postId));
    }

    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") String commentId) {
        commentService.deleteComment(Long.parseLong(commentId));
        return ResponseEntity.ok("Comment deleted");
    }


}
