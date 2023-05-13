package ru.veselov.instazoocource.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.instazoocource.entity.UserEntity;
import ru.veselov.instazoocource.entity.enums.ERole;
import ru.veselov.instazoocource.exception.UserAlreadyExistsException;
import ru.veselov.instazoocource.mapper.UserMapper;
import ru.veselov.instazoocource.mapper.UserMapperImpl;
import ru.veselov.instazoocource.payload.request.SignUpRequest;
import ru.veselov.instazoocource.repository.UserRepository;
import ru.veselov.instazoocource.util.UserUtils;

import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Captor
    ArgumentCaptor<UserEntity> userEntityCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(userService, "userMapper", new UserMapperImpl(), UserMapper.class);
        ReflectionTestUtils.setField(userService,
                "passwordEncoder",
                new BCryptPasswordEncoder(),
                PasswordEncoder.class);
    }

    @Test
    void shouldCreateUser() {
        SignUpRequest signUpRequest = UserUtils.getSignUpRequest();
        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        userService.createUser(signUpRequest);

        verify(userRepository, times(1)).save(userEntityCaptor.capture());
        UserEntity captured = userEntityCaptor.getValue();
        Assertions.assertThat(captured.getPassword()).isNotNull().isNotEqualTo(signUpRequest.getPassword());
        Assertions.assertThat(captured.getUsername()).isEqualTo(signUpRequest.getUsername());
        Assertions.assertThat(captured.getRoles()).hasSize(1).contains(ERole.ROLE_USER);
        Assertions.assertThat(captured.getEmail()).isEqualTo(signUpRequest.getEmail());
        Assertions.assertThat(captured.getFirstname()).isEqualTo(signUpRequest.getFirstname());
        Assertions.assertThat(captured.getLastname()).isEqualTo(signUpRequest.getLastname());
    }

    @Test
    void shouldThrowException() {
        UserEntity userEntity = UserUtils.getUserEntity();
        SignUpRequest signUpRequest = UserUtils.getSignUpRequest();
        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));
        when(userRepository.findUserByEmail(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                userService.createUser(signUpRequest)
        ).isInstanceOf(UserAlreadyExistsException.class);

        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));

        Assertions.assertThatThrownBy(() ->
                userService.createUser(signUpRequest)
        ).isInstanceOf(UserAlreadyExistsException.class);

        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));
        when(userRepository.findUserByEmail(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));

        Assertions.assertThatThrownBy(() ->
                userService.createUser(signUpRequest)
        ).isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, never()).save(ArgumentMatchers.any());
    }

}