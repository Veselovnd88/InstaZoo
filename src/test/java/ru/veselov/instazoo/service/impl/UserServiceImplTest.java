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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.instazoo.dto.UserDTO;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.entity.enums.ERole;
import ru.veselov.instazoo.exception.UserAlreadyExistsException;
import ru.veselov.instazoo.exception.UserNotFoundException;
import ru.veselov.instazoo.mapper.UserMapper;
import ru.veselov.instazoo.mapper.UserMapperImpl;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.payload.request.SignUpRequest;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

import java.security.Principal;
import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @Mock
    Principal principal;

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
        SignUpRequest signUpRequest = TestUtils.getSignUpRequest();
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
        UserEntity userEntity = TestUtils.getUserEntity();
        SignUpRequest signUpRequest = TestUtils.getSignUpRequest();
        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));
        when(userRepository.findUserByEmail(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.createUser(signUpRequest))
                .isInstanceOf(UserAlreadyExistsException.class);

        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));

        Assertions.assertThatThrownBy(() -> userService.createUser(signUpRequest))
                .isInstanceOf(UserAlreadyExistsException.class);

        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));
        when(userRepository.findUserByEmail(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));

        Assertions.assertThatThrownBy(() -> userService.createUser(signUpRequest))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void shouldUpdateUser() {
        UserDTO userDTO = TestUtils.getUserDTO();
        UserEntity userEntity = TestUtils.getUserEntity();
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.of(userEntity));

        userService.updateUser(userDTO, principal);

        verify(userRepository, times(1)).save(userEntityCaptor.capture());
        UserEntity captured = userEntityCaptor.getValue();
        Assertions.assertThat(captured.getLastname()).isEqualTo(userDTO.getLastname());
        Assertions.assertThat(captured.getBio()).isEqualTo(userDTO.getBio());
        Assertions.assertThat(captured.getFirstname()).isEqualTo(userDTO.getFirstname());
        Assertions.assertThat(captured.getUsername()).isEqualTo(userEntity.getUsername());
    }

    @Test
    void shouldReturnCurrentUser() {
        UserEntity userEntity = TestUtils.getUserEntity();
        when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.of(userEntity));
        when(principal.getName()).thenReturn(Constants.USERNAME);

        User currentUser = userService.getCurrentUser(principal);

        Assertions.assertThat(currentUser.getFirstname()).isEqualTo(userEntity.getFirstname());
        Assertions.assertThat(currentUser.getLastname()).isEqualTo(userEntity.getLastname());
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        UserDTO userDTO = TestUtils.getUserDTO();
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.updateUser(userDTO, principal))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionIfUserNotFoundForGettingCurrentUser() {
        when(principal.getName()).thenReturn(Constants.USERNAME);
        when(userRepository.findUserByUsername(Constants.USERNAME)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.getCurrentUser(principal))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldFindUserById() {
        UserEntity userEntity = TestUtils.getUserEntity();
        when(userRepository.findUserById(Constants.ANY_ID)).thenReturn(Optional.of(userEntity));

        User currentUser = userService.getUserById(Constants.ANY_ID);

        verify(userRepository, times(1)).findUserById(Constants.ANY_ID);
        Assertions.assertThat(currentUser.getFirstname()).isEqualTo(userEntity.getFirstname());
        Assertions.assertThat(currentUser.getLastname()).isEqualTo(userEntity.getLastname());
    }

    @Test
    void shouldThrowExceptionIfNoUserByIdFound() {
        when(userRepository.findUserById(Constants.ANY_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.getUserById(Constants.ANY_ID))
                .isInstanceOf(UserNotFoundException.class);
    }

}