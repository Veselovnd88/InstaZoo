package ru.veselov.instazoocource.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.veselov.instazoocource.entity.UserEntity;
import ru.veselov.instazoocource.mapper.UserMapper;
import ru.veselov.instazoocource.model.User;
import ru.veselov.instazoocource.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findUserByUsername(username)
                .orElseThrow(
                        () -> {
                            log.error("No such user with [username {}", username);
                            throw new UsernameNotFoundException(
                                    String.format("No such user with this [username %s]", username));
                        }
                );
        log.info("Retrieving [user {} from repo", username);
        return buildUserDetails(user);
    }

    public User loadUserById(Long id) {
        UserEntity userEntity = userRepository.findUserById(id).orElseThrow(
                () -> {
                    log.error("No such user found with [id {}", id);
                    throw new EntityNotFoundException(
                            String.format("No such user with [id %s]", id));
                }
        );
        log.info("Retrieving [User {}] from repo", id);
        return buildUserDetails(userEntity);
    }

    private User buildUserDetails(UserEntity userEntity) {
        List<GrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
        User user = userMapper.entityToUser(userEntity);
        user.setAuthorities(authorities);
        return user;
    }

}
