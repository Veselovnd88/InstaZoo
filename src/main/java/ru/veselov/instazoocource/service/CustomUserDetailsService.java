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
import ru.veselov.instazoocource.entity.User;
import ru.veselov.instazoocource.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username)
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
        return userRepository.findUserById(id).orElseThrow(
                () -> {
                    log.error("No such user found with [id {}", id);
                    throw new EntityNotFoundException(
                            String.format("No such user with [id %s]", id));
                }
        );
    }

    public static User buildUserDetails(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
        return new User(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
