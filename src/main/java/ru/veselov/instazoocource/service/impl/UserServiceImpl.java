package ru.veselov.instazoocource.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.instazoocource.dto.UserDTO;
import ru.veselov.instazoocource.entity.UserEntity;
import ru.veselov.instazoocource.entity.enums.ERole;
import ru.veselov.instazoocource.exception.UserAlreadyExistsException;
import ru.veselov.instazoocource.mapper.UserMapper;
import ru.veselov.instazoocource.model.User;
import ru.veselov.instazoocource.payload.request.SignUpRequest;
import ru.veselov.instazoocource.repository.UserRepository;
import ru.veselov.instazoocource.service.UserService;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    @Transactional
    public void createUser(SignUpRequest signUp) {
        String username = signUp.getUsername();
        Optional<UserEntity> userByUsername = userRepository.findUserByUsername(username);
        Optional<UserEntity> userByEmail = userRepository.findUserByEmail(signUp.getEmail());
        if (userByUsername.isPresent() || userByEmail.isPresent()) {
            log.error(
                    "User with this [username {} or email {}] already exists",
                    signUp.getUsername(),
                    signUp.getEmail());
            throw new UserAlreadyExistsException(
                    String.format("User with this [username %s or email %s] already exists",
                            signUp.getUsername(),
                            signUp.getEmail()));
        }
        UserEntity user = userMapper.signUpToUser(signUp);
        user.setPassword(passwordEncoder.encode(signUp.getPassword()));
        user.getRoles().add(ERole.ROLE_USER);
        userRepository.save(user);
        log.info("User with [username {}] successfully created and saved", username);
    }

    @Override
    public User updateUser(UserDTO userDTO, Principal principal) {
        UserEntity userFromPrincipal = getUserByPrincipal(principal);
        userFromPrincipal.setFirstname(userDTO.getFirstname());
        userFromPrincipal.setLastname(userDTO.getLastname());
        userFromPrincipal.setBio(userDTO.getBio());
        UserEntity saved = userRepository.save(userFromPrincipal);
        return userMapper.entityToUser(saved);
    }

    @Override
    public User getCurrentUser(Principal principal) {
        UserEntity userFromPrincipal = getUserByPrincipal(principal);
        return userMapper.entityToUser(userFromPrincipal);
    }

    public UserEntity getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> {
                    log.error("User with such [username {}] not found", username);
                    throw new UsernameNotFoundException(
                            String.format("User with such [username %s] not found", username)
                    );
                }
        );
    }

}