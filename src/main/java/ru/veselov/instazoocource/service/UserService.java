package ru.veselov.instazoocource.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.veselov.instazoocource.entity.User;
import ru.veselov.instazoocource.entity.enums.ERole;
import ru.veselov.instazoocource.exception.UserAlreadyExistsException;
import ru.veselov.instazoocource.mapper.UserMapper;
import ru.veselov.instazoocource.payload.request.SignUpRequest;
import ru.veselov.instazoocource.repository.UserRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public User createUser(SignUpRequest signUp) {
        String username = signUp.getUsername();
        Optional<User> userByUsername = userRepository.findUserByUsername(username);
        if (userByUsername.isPresent()) {
            log.error("User with this [username {}] already exists", userByUsername);
            throw new UserAlreadyExistsException(
                    String.format("User with this [username %s] already exists", userByUsername));
        }
        User user = userMapper.signUpToUser(signUp);
        user.setPassword(passwordEncoder.encode(signUp.getPassword()));
        user.getRoles().add(ERole.ROLE_USER);
        log.info("User with [username {}] successfully created", username);
        return userRepository.save(user);
    }

}
