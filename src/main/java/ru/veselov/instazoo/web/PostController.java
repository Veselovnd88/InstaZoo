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
import ru.veselov.instazoo.dto.PostDTO;
import ru.veselov.instazoo.model.Post;
import ru.veselov.instazoo.payload.response.ResponseMessage;
import ru.veselov.instazoo.service.PostService;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final FieldErrorResponseService fieldErrorResponseService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@Valid @RequestBody PostDTO postDTO, BindingResult bindingResult, Principal principal) {
        fieldErrorResponseService.validateFields(bindingResult);
        return postService.createPost(postDTO, principal);
    }

    @GetMapping("/all")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping
    public List<Post> getAllPostsForUser(Principal principal) {
        return postService.getAllPostsForUser(principal);
    }

    @PostMapping("/{postId}/{username}/like")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Post likePost(@PathVariable("postId") String postId,
                         @PathVariable("username") String username) {
        return postService.likePost(Long.parseLong(postId), username);
    }

    @DeleteMapping("/{postId}/delete")
    public ResponseMessage deletePost(@PathVariable("postId") String postId, Principal principal) {
        postService.deletePost(Long.parseLong(postId), principal);
        return new ResponseMessage(String.format("Post %s deleted", postId));
    }

}