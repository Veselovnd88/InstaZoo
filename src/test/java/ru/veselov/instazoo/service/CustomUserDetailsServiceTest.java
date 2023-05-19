package ru.veselov.instazoo.service;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.mapper.UserMapper;
import ru.veselov.instazoo.mapper.UserMapperImpl;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(customUserDetailsService, "userMapper", new UserMapperImpl(), UserMapper.class);
    }

    @Test
    void shouldThrowExceptionIfNoSuchUsername() {
        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(Constants.USERNAME))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldReturnCorrectUserDetails() {
        UserEntity userEntity = TestUtils.getUserEntity();
        when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(Constants.USERNAME);

        Assertions.assertThat(userDetails).isNotNull().isInstanceOf(User.class);
        Assertions.assertThat(userDetails.getUsername()).isEqualTo(userEntity.getUsername());
        Assertions.assertThat(userDetails.getPassword()).isEqualTo(userEntity.getPassword());
        Assertions.assertThat(userDetails.getAuthorities()).hasSize(2);
    }

    @Test
    void shouldThrowEntityNotFoundException() {
        when(userRepository.findUserById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> customUserDetailsService.loadUserById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldReturnUserById() {
        UserEntity userEntity = TestUtils.getUserEntity();
        when(userRepository.findUserById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = customUserDetailsService.loadUserById(1L);

        Assertions.assertThat(userDetails).isNotNull().isInstanceOf(User.class);
        Assertions.assertThat(userDetails.getUsername()).isEqualTo(userEntity.getUsername());
        Assertions.assertThat(userDetails.getPassword()).isEqualTo(userEntity.getPassword());
        Assertions.assertThat(userDetails.getAuthorities()).hasSize(2);
    }

}