package ru.veselov.instazoo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.instazoo.dto.CommentDTO;
import ru.veselov.instazoo.entity.CommentEntity;
import ru.veselov.instazoo.entity.PostEntity;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.exception.PostNotFoundException;
import ru.veselov.instazoo.mapper.CommentMapper;
import ru.veselov.instazoo.model.Comment;
import ru.veselov.instazoo.repository.CommentRepository;
import ru.veselov.instazoo.repository.PostRepository;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.service.CommentService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public Comment saveComment(Long postId, CommentDTO commentDTO, Principal principal) {
        UserEntity userEntity = getUserByPrincipal(principal);
        PostEntity post = getPostById(postId);
        CommentEntity commentEntity = commentMapper.dtoToEntity(commentDTO);
        commentEntity.setPost(post);
        commentEntity.setUserId(userEntity.getId());
        CommentEntity saved = commentRepository.save(commentEntity);
        return commentMapper.entityToComment(saved);
    }

    @Override
    public List<Comment> getAllCommentsForPost(Long postId) {
        PostEntity post = getPostById(postId);
        List<CommentEntity> comments = commentRepository.findAllByPost(post);
        return commentMapper.entitiesToComments(comments);
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId) {
        Optional<CommentEntity> commentOptional = commentRepository.findById(commentId);
        commentOptional.ifPresent(commentRepository::delete);
    }

    private UserEntity getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> {
                    log.error("User with such [username {}] not found", username);
                    throw new UsernameNotFoundException(
                            String.format("User with such [username %s] not found", username)
                    );
                }
        );
    }

    private PostEntity getPostById(Long postId) {
        Optional<PostEntity> foundPost = postRepository.findById(postId);
        return foundPost.orElseThrow(() -> {
            log.error("[Post with id {}] not found", postId);
            throw new PostNotFoundException(
                    String.format("[Post with id %s] not found", postId));
        });
    }

}