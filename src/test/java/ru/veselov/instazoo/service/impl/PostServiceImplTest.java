package ru.veselov.instazoo.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.instazoo.dto.PostDTO;
import ru.veselov.instazoo.entity.ImageEntity;
import ru.veselov.instazoo.entity.PostEntity;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.exception.PostNotFoundException;
import ru.veselov.instazoo.mapper.PostMapper;
import ru.veselov.instazoo.mapper.PostMapperImpl;
import ru.veselov.instazoo.repository.ImageRepository;
import ru.veselov.instazoo.repository.PostRepository;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

import java.security.Principal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    ImageRepository imageRepository;

    @Mock
    Principal principal;

    @InjectMocks
    PostServiceImpl postService;

    @Captor
    ArgumentCaptor<PostEntity> postCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(postService, "postMapper", new PostMapperImpl(), PostMapper.class);
    }

    @Test
    void shouldCreatePost() {
        PostDTO postDTO = TestUtils.getPostDTO();
        UserEntity userEntity = TestUtils.getUserEntity();
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));

        postService.createPost(postDTO, principal);

        verify(postRepository, times(1)).save(postCaptor.capture());
        verify(userRepository, times(1)).findUserByUsername(ArgumentMatchers.anyString());
        PostEntity captured = postCaptor.getValue();
        Assertions.assertThat(captured.getLikes()).isZero();
        Assertions.assertThat(captured.getTitle()).isEqualTo(postDTO.getTitle());
        Assertions.assertThat(captured.getCaption()).isEqualTo(postDTO.getCaption());
        Assertions.assertThat(captured.getLocation()).isEqualTo(postDTO.getLocation());
        Assertions.assertThat(captured.getUsername()).isEqualTo(userEntity.getUsername());
    }

    @Test
    void shouldReturnListOfPosts() {
        postService.getAllPosts();

        Mockito.verify(postRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void shouldReturnPostById() {
        UserEntity userEntity = TestUtils.getUserEntity();
        PostEntity postEntity = TestUtils.getPostEntity();
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));
        when(postRepository.findPostByIdAndUser(
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(UserEntity.class))
        ).thenReturn(Optional.of(postEntity));

        Assertions.assertThatNoException().isThrownBy(() ->
                postService.getPostById(Constants.ANY_ID, principal));

        verify(postRepository, times(1)).findPostByIdAndUser(
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(UserEntity.class));
        verify(userRepository, times(1)).findUserByUsername(ArgumentMatchers.anyString());
    }

    @Test
    void shouldThrowExceptionIfPostNoFound() {
        UserEntity userEntity = TestUtils.getUserEntity();
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));
        when(postRepository.findPostByIdAndUser(ArgumentMatchers.anyLong(), ArgumentMatchers.any(UserEntity.class))
        ).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                postService.getPostById(Constants.ANY_ID, principal)).isInstanceOf(PostNotFoundException.class);

        verify(postRepository, times(1)).findPostByIdAndUser(
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(UserEntity.class));
        verify(userRepository, times(1)).findUserByUsername(ArgumentMatchers.anyString());
    }

    @Test
    void shouldReturnPostsForUser() {
        UserEntity userEntity = TestUtils.getUserEntity();
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));

        postService.getAllPostsForUser(principal);

        verify(postRepository, times(1)).findAllByUserOrderByCreatedAtDesc(userEntity);
    }

    @Test
    void shouldAddLikeAndLikedUser() {
        PostEntity postEntity = TestUtils.getPostEntity();
        String newLikedUser = "User didn't like it before";
        when(postRepository.findById(Constants.ANY_ID)).thenReturn(Optional.of(postEntity));
        int likesBefore = postEntity.getLikes();
        postService.likePost(Constants.ANY_ID, newLikedUser);

        Assertions.assertThat(postEntity.getLikes()).isEqualTo(likesBefore + 1);
        Assertions.assertThat(postEntity.getLikedUsers()).contains(newLikedUser);

        verify(postRepository, times(1)).save(postEntity);
    }

    @Test
    void shouldRemoveLikeAndLikedUser() {
        PostEntity postEntity = TestUtils.getPostEntity();
        String alreadyLikedUser = "BlackDog";
        when(postRepository.findById(Constants.ANY_ID)).thenReturn(Optional.of(postEntity));
        int likesBefore = postEntity.getLikes();
        postService.likePost(Constants.ANY_ID, alreadyLikedUser);

        Assertions.assertThat(postEntity.getLikes()).isEqualTo(likesBefore - 1);
        Assertions.assertThat(postEntity.getLikedUsers()).doesNotContain(alreadyLikedUser);

        verify(postRepository, times(1)).save(postEntity);
    }

    @Test
    void shouldThrowExceptionIfNoPostForLikeFound() {
        when(postRepository.findById(Constants.ANY_ID)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> postService.likePost(Constants.ANY_ID, Constants.USERNAME)
        ).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void shouldDeletePostAndImage() {
        UserEntity userEntity = TestUtils.getUserEntity();
        PostEntity postEntity = TestUtils.getPostEntity();
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));
        when(postRepository.findPostByIdAndUser(ArgumentMatchers.anyLong(), ArgumentMatchers.any(UserEntity.class))
        ).thenReturn(Optional.of(postEntity));
        ImageEntity imageEntity = new ImageEntity();
        when(imageRepository.findByPostId(postEntity.getId())).thenReturn(Optional.of(imageEntity));

        postService.deletePost(postEntity.getId(), principal);

        verify(postRepository, times(1)).delete(postEntity);
        verify(imageRepository, times(1)).delete(imageEntity);
    }

    @Test
    void shouldThrowExceptionIfNoUserFound() {
        PostDTO postDTO = TestUtils.getPostDTO();
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                postService.createPost(postDTO, principal)).isInstanceOf(UsernameNotFoundException.class);

        Assertions.assertThatThrownBy(() ->
                postService.deletePost(1L, principal)).isInstanceOf(UsernameNotFoundException.class);

        Assertions.assertThatThrownBy(() ->
                postService.getAllPostsForUser(principal)).isInstanceOf(UsernameNotFoundException.class);
    }

}