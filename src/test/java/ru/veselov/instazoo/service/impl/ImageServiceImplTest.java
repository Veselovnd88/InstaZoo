package ru.veselov.instazoo.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.instazoo.entity.ImageEntity;
import ru.veselov.instazoo.entity.PostEntity;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.exception.ImageNotFoundException;
import ru.veselov.instazoo.exception.ImageProcessingException;
import ru.veselov.instazoo.mapper.ImageMapper;
import ru.veselov.instazoo.mapper.ImageMapperImpl;
import ru.veselov.instazoo.repository.ImageRepository;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.zip.Deflater;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ImageRepository imageRepository;

    @Mock
    Principal principal;

    @InjectMocks
    ImageServiceImpl imageService;

    @Captor
    ArgumentCaptor<ImageEntity> imageCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(imageService, "imageMapper", new ImageMapperImpl(), ImageMapper.class);
    }

    @Test
    void shouldUploadImageToUserInImageExists() {
        MockMultipartFile multipartFile = new MockMultipartFile("File", "OrigName", "image", new byte[]{1, 2, 3, 4});
        ImageEntity imageEntity = TestUtils.getImageEntity();
        UserEntity userEntity = TestUtils.getUserEntity();
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.of(userEntity));
        when(imageRepository.findByUserId(userEntity.getId())).thenReturn(Optional.of(imageEntity));

        imageService.uploadImageToUser(multipartFile, principal);

        verify(imageRepository, times(1)).delete(imageEntity);
        verify(imageRepository, times(1)).save(imageCaptor.capture());
        verify(userRepository, times(1)).findUserByUsername(Constants.USERNAME);
        ImageEntity captured = imageCaptor.getValue();

        Assertions.assertThat(captured.getUserId()).isEqualTo(userEntity.getId());
        Assertions.assertThat(captured.getName()).isEqualTo(multipartFile.getOriginalFilename());
    }

    @Test
    void shouldUploadImageToUserInNoImage() {
        MockMultipartFile multipartFile = new MockMultipartFile("File", "OrigName", "image", new byte[]{1, 2, 3, 4});
        ImageEntity imageEntity = TestUtils.getImageEntity();
        UserEntity userEntity = TestUtils.getUserEntity();
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.of(userEntity));
        when(imageRepository.findByUserId(userEntity.getId())).thenReturn(Optional.empty());

        imageService.uploadImageToUser(multipartFile, principal);

        verify(imageRepository, never()).delete(imageEntity);
        verify(imageRepository, times(1)).save(imageCaptor.capture());
        verify(userRepository, times(1)).findUserByUsername(Constants.USERNAME);
        ImageEntity captured = imageCaptor.getValue();

        Assertions.assertThat(captured.getUserId()).isEqualTo(userEntity.getId());
        Assertions.assertThat(captured.getName()).isEqualTo(multipartFile.getOriginalFilename());
    }

    @Test
    void shouldUploadImageToPost() {
        MockMultipartFile multipartFile = new MockMultipartFile("File", "OrigName", "image", new byte[]{1, 2, 3, 4});
        UserEntity userEntity = TestUtils.getUserEntity();
        PostEntity postEntity = TestUtils.getPostEntity();
        PostEntity postEntity2 = new PostEntity();
        postEntity2.setId(2L);
        userEntity.setPosts(List.of(postEntity, postEntity2));
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.of(userEntity));

        imageService.uploadImageToPost(multipartFile, principal, Constants.ANY_ID);

        verify(imageRepository, times(1)).save(imageCaptor.capture());
        ImageEntity captured = imageCaptor.getValue();

        Assertions.assertThat(captured.getName()).isEqualTo(multipartFile.getOriginalFilename());
    }

    @Test
    void shouldGetImageToUser() {
        UserEntity userEntity = TestUtils.getUserEntity();
        ImageEntity imageEntity = TestUtils.getImageEntity();
        imageEntity.setImageBytes(compressBytes(imageEntity.getImageBytes()));
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.of(userEntity));
        when(imageRepository.findByUserId(Constants.ANY_ID)).thenReturn(Optional.of(imageEntity));

        imageService.getImageToUser(principal);

        verify(imageRepository, times(1)).findByUserId(Constants.ANY_ID);
        verify(userRepository, times(1)).findUserByUsername(Constants.USERNAME);
    }

    @Test
    void getImageToPost() {
        ImageEntity imageEntity = TestUtils.getImageEntity();
        imageEntity.setImageBytes(compressBytes(imageEntity.getImageBytes()));
        when(imageRepository.findByPostId(Constants.ANY_ID)).thenReturn(Optional.of(imageEntity));

        imageService.getImageToPost(Constants.ANY_ID);

        verify(imageRepository, times(1)).findByPostId(Constants.ANY_ID);
    }

    @Test
    void shouldThrowImageNotFoundException() {
        UserEntity userEntity = TestUtils.getUserEntity();
        ImageEntity imageEntity = TestUtils.getImageEntity();
        imageEntity.setImageBytes(compressBytes(imageEntity.getImageBytes()));
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.of(userEntity));
        when(imageRepository.findByUserId(Constants.ANY_ID)).thenReturn(Optional.empty());
        when(imageRepository.findByPostId(Constants.ANY_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> imageService.getImageToUser(principal))
                .isInstanceOf(ImageNotFoundException.class);
        Assertions.assertThatThrownBy(() -> imageService.getImageToPost(Constants.ANY_ID))
                .isInstanceOf(ImageNotFoundException.class);
    }

    @Test
    void shouldThrowUserNameNotFoundException() {
        MockMultipartFile multipartFile = new MockMultipartFile("File", "OrigName", "image", new byte[]{1, 2, 3, 4});
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> imageService.uploadImageToUser(multipartFile, principal))
                .isInstanceOf(UsernameNotFoundException.class);
        Assertions.assertThatThrownBy(() -> imageService.uploadImageToPost(multipartFile, principal, Constants.ANY_ID))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldThrowImageProcessingException() {
        UserEntity userEntity = TestUtils.getUserEntity();
        ImageEntity imageEntity = TestUtils.getImageEntity();
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.of(userEntity));
        when(imageRepository.findByUserId(Constants.ANY_ID)).thenReturn(Optional.of(imageEntity));
        when(imageRepository.findByPostId(Constants.ANY_ID)).thenReturn(Optional.of(imageEntity));

        Assertions.assertThatThrownBy(() -> imageService.getImageToPost(Constants.ANY_ID))
                .isInstanceOf(ImageProcessingException.class);
        Assertions.assertThatThrownBy(() -> imageService.getImageToUser(principal))
                .isInstanceOf(ImageProcessingException.class);
    }

    private static byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            baos.write(buffer, 0, count);
        }
        try {
            baos.close();
        } catch (IOException e) {
            throw new ImageProcessingException("Compression error: " + e.getMessage());
        }
        return baos.toByteArray();
    }

}