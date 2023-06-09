package ru.veselov.instazoo.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.instazoo.dto.CommentDTO;
import ru.veselov.instazoo.entity.CommentEntity;
import ru.veselov.instazoo.entity.PostEntity;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.exception.PostNotFoundException;
import ru.veselov.instazoo.mapper.CommentMapper;
import ru.veselov.instazoo.mapper.CommentMapperImpl;
import ru.veselov.instazoo.repository.CommentRepository;
import ru.veselov.instazoo.repository.PostRepository;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

import java.security.Principal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    Principal principal;

    @InjectMocks
    CommentServiceImpl commentService;

    @Captor
    ArgumentCaptor<CommentEntity> commentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(commentService, "commentMapper", new CommentMapperImpl(), CommentMapper.class);
    }

    @Test
    void shouldSaveComment() {
        CommentDTO commentDTO = TestUtils.getCommentDTO();
        UserEntity userEntity = TestUtils.getUserEntity();
        PostEntity postEntity = TestUtils.getPostEntity();
        CommentEntity commentEntity = TestUtils.getCommentEntity();
        Mockito.when(principal.getName()).thenReturn(Constants.USERNAME);
        Mockito.when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.of(userEntity));
        Mockito.when(postRepository.findById(Constants.ANY_ID)).thenReturn(Optional.of(postEntity));
        Mockito.when(commentRepository.save(ArgumentMatchers.any())).thenReturn(commentEntity);

        commentService.saveComment(Constants.ANY_ID, commentDTO, principal);

        Mockito.verify(commentRepository, Mockito.times(1)).save(commentCaptor.capture());
        CommentEntity captured = commentCaptor.getValue();
        Assertions.assertThat(captured.getMessage()).isEqualTo(commentDTO.getMessage());
        Assertions.assertThat(captured.getPost()).isEqualTo(postEntity);
        Assertions.assertThat(captured.getUserId()).isEqualTo(userEntity.getId());
        Assertions.assertThat(captured.getUsername()).isEqualTo(userEntity.getUsername());
    }

    @Test
    void shouldGetAllCommentsToPost() {
        PostEntity postEntity = TestUtils.getPostEntity();
        Mockito.when(postRepository.findById(Constants.ANY_ID)).thenReturn(Optional.of(postEntity));

        commentService.getAllCommentsForPost(Constants.ANY_ID);

        Mockito.verify(commentRepository, Mockito.times(1)).findAllByPost(postEntity);
    }

    @Test
    void shouldDeleteComment() {
        CommentEntity commentEntity = TestUtils.getCommentEntity();
        Mockito.when(commentRepository.findById(Constants.ANY_ID)).thenReturn(Optional.of(commentEntity));

        commentService.deleteComment(Constants.ANY_ID);

        Mockito.verify(commentRepository, Mockito.times(1)).delete(commentEntity);
    }

    @Test
    void shouldThrowExceptionIfUsernameNotFound() {
        CommentDTO commentDTO = TestUtils.getCommentDTO();
        Mockito.when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.empty());
        Mockito.when(principal.getName()).thenReturn(Constants.USERNAME);
        Assertions.assertThatThrownBy(() -> commentService.saveComment(Constants.ANY_ID, commentDTO, principal))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionIfPostNotFound() {
        CommentDTO commentDTO = TestUtils.getCommentDTO();
        Mockito.when(postRepository.findById(Constants.ANY_ID)).thenReturn(Optional.empty());
        Mockito.when(principal.getName()).thenReturn(Constants.USERNAME);
        Mockito.when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.of(TestUtils.getUserEntity()));

        Assertions.assertThatThrownBy(() -> commentService.saveComment(Constants.ANY_ID, commentDTO, principal))
                .isInstanceOf(PostNotFoundException.class);

        Assertions.assertThatThrownBy(() ->
                commentService.getAllCommentsForPost(Constants.ANY_ID)).isInstanceOf(PostNotFoundException.class);
    }

}
