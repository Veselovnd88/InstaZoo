package ru.veselov.instazoocource.service;

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
import ru.veselov.instazoocource.entity.UserEntity;
import ru.veselov.instazoocource.mapper.UserMapper;
import ru.veselov.instazoocource.mapper.UserMapperImpl;
import ru.veselov.instazoocource.model.User;
import ru.veselov.instazoocource.repository.UserRepository;
import ru.veselov.instazoocource.util.Constants;
import ru.veselov.instazoocource.util.UserUtils;

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

        Assertions.assertThatThrownBy(() ->
                customUserDetailsService.loadUserByUsername(Constants.USERNAME)
        ).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldReturnCorrectUserDetails() {
        UserEntity userEntity = UserUtils.getUserEntity();
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

        Assertions.assertThatThrownBy(() ->
                customUserDetailsService.loadUserById(1L)
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldReturnUserById() {
        UserEntity userEntity = UserUtils.getUserEntity();
        when(userRepository.findUserById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = customUserDetailsService.loadUserById(1L);

        Assertions.assertThat(userDetails).isNotNull().isInstanceOf(User.class);
        Assertions.assertThat(userDetails.getUsername()).isEqualTo(userEntity.getUsername());
        Assertions.assertThat(userDetails.getPassword()).isEqualTo(userEntity.getPassword());
        Assertions.assertThat(userDetails.getAuthorities()).hasSize(2);
    }

}