package ru.veselov.instazoo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.instazoo.dto.PostDTO;
import ru.veselov.instazoo.entity.ImageEntity;
import ru.veselov.instazoo.entity.PostEntity;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.exception.PostNotFoundException;
import ru.veselov.instazoo.mapper.PostMapper;
import ru.veselov.instazoo.model.Post;
import ru.veselov.instazoo.repository.ImageRepository;
import ru.veselov.instazoo.repository.PostRepository;
import ru.veselov.instazoo.service.PostService;
import ru.veselov.instazoo.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostServiceImpl implements PostService {

    private final UserService userService;

    private final PostRepository postRepository;

    private final ImageRepository imageRepository;

    private final PostMapper postMapper;

    @Override
    @Transactional
    public Post createPost(PostDTO postDTO, Principal principal) {
        UserEntity foundUserEntity = userService.getUserByPrincipal(principal);
        PostEntity post = postMapper.toEntity(postDTO);
        post.setUser(foundUserEntity);
        post.setLikes(0);
        PostEntity saved = postRepository.save(post);
        log.info("[Post #{}] saved to repository", post.getId());
        return postMapper.entityToPost(saved);
    }

    @Override
    public List<Post> getAllPosts() {
        log.info("Retrieving all posts from repository");
        return postMapper.entitiesToPosts(postRepository.findAllByOrderByCreatedAtDesc());
    }

    @Override
    public Post getPostById(Long postId, Principal principal) {
        PostEntity postEntity = getPostByIdAndPrincipal(postId, principal);
        log.info("Retrieving [post {} of user {}]", postId, principal.getName());
        return postMapper.entityToPost(postEntity);
    }

    @Override
    public List<Post> getAllPostsForUser(Principal principal) {
        UserEntity foundUser = userService.getUserByPrincipal(principal);
        List<PostEntity> posts = postRepository.findAllByUserOrderByCreatedAtDesc(foundUser);
        log.info("Retrieving all posts of [user {}]", principal.getName());
        return postMapper.entitiesToPosts(posts);
    }

    @Override
    @Transactional
    public Post likePost(Long postId, String username) {
        Optional<PostEntity> foundPost = postRepository.findById(postId);
        PostEntity post = foundPost.orElseThrow(() -> {
            log.error("[Post with id {}] not found", postId);
            throw new PostNotFoundException(
                    String.format("[Post with id %s] not found", postId));
        });
        Optional<String> likedUser = post.getLikedUsers().stream().filter(u -> u.equals(username)).findAny();
        if (likedUser.isPresent()) {
            post.getLikedUsers().remove(username);
            post.setLikes(post.getLikes() - 1);
            log.info("Like of [user {}] removed from [post {}]", username, postId);
        } else {
            post.getLikedUsers().add(username);
            post.setLikes(post.getLikes() + 1);
            log.info("Like of [user {}] added to [post {}]", username, postId);
        }
        PostEntity saved = postRepository.save(post);
        log.info("[Post {}] with new number of likes saved", postId);
        return postMapper.entityToPost(saved);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Principal principal) {
        PostEntity postEntity = getPostByIdAndPrincipal(postId, principal);
        Optional<ImageEntity> imageOptional = imageRepository.findByPostId(postEntity.getId());
        postRepository.delete(postEntity);
        imageOptional.ifPresent(imageRepository::delete);
        log.info("[Post {}] was deleted with image", postId);
    }

    private PostEntity getPostByIdAndPrincipal(Long postId, Principal principal) {
        UserEntity foundUser = userService.getUserByPrincipal(principal);
        Optional<PostEntity> foundPost = postRepository.findPostByIdAndUser(postId, foundUser);
        return foundPost.orElseThrow(() -> {
            log.error("[Post with id {} and user {}] not found", postId, principal.getName());
            throw new PostNotFoundException(
                    String.format("[Post with id %s and user %s] not found", postId, principal.getName()));
        });
    }

}