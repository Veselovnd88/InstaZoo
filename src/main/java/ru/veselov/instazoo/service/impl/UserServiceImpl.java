package ru.veselov.instazoo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.instazoo.dto.UserDTO;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.entity.enums.ERole;
import ru.veselov.instazoo.exception.UserAlreadyExistsException;
import ru.veselov.instazoo.exception.UserNotFoundException;
import ru.veselov.instazoo.mapper.UserMapper;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.payload.request.SignUpRequest;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.service.UserService;

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

    @Transactional
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

    @Override
    public User getUserById(Long userId) {
        UserEntity userEntity = userRepository.findUserById(userId).orElseThrow(() -> {
            log.error("User with such [userId {}] not found", userId);
            throw new UserNotFoundException(
                    String.format("User with such [useId %s] not found", userId)
            );
        });
        return userMapper.entityToUser(userEntity);
    }


    private UserEntity getUserByPrincipal(Principal principal) {
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