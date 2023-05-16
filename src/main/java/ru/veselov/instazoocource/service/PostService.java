package ru.veselov.instazoocource.service;

import ru.veselov.instazoocource.dto.PostDTO;
import ru.veselov.instazoocource.model.Post;

import java.security.Principal;
import java.util.List;

public interface PostService {
    Post createPost(PostDTO postDTO, Principal principal);

    List<Post> getAllPosts();

    Post getPostById(Long postId, Principal principal);

    List<Post> getAllPostsForUser(Principal principal);

    Post likePost(Long postId, String username);

}
