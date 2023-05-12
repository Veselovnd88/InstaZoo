package ru.veselov.instazoocource.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.instazoocource.entity.UserEntity;
import ru.veselov.instazoocource.entity.enums.ERole;
import ru.veselov.instazoocource.exception.UserAlreadyExistsException;
import ru.veselov.instazoocource.mapper.UserMapper;
import ru.veselov.instazoocource.payload.request.SignUpRequest;
import ru.veselov.instazoocource.repository.UserRepository;
import ru.veselov.instazoocource.service.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    @Transactional
    public UserEntity createUser(SignUpRequest signUp) {
        String username = signUp.getUsername();
        Optional<UserEntity> userByUsername = userRepository.findUserByUsername(username);
        if (userByUsername.isPresent()) {
            log.error("User with this [username {}] already exists", signUp.getUsername());
            throw new UserAlreadyExistsException(
                    String.format("User with this [username %s] already exists", signUp.getUsername()));
        }
        UserEntity user = userMapper.signUpToUser(signUp);
        user.setPassword(passwordEncoder.encode(signUp.getPassword()));
        user.getRoles().add(ERole.ROLE_USER);
        log.info("User with [username {}] successfully created", username);
        return userRepository.save(user);
    }

}