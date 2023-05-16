package ru.veselov.instazoocource.service;

import ru.veselov.instazoocource.dto.UserDTO;
import ru.veselov.instazoocource.entity.UserEntity;
import ru.veselov.instazoocource.model.User;
import ru.veselov.instazoocource.payload.request.SignUpRequest;

import java.security.Principal;

public interface UserService {

    void createUser(SignUpRequest signUp);

    User updateUser(UserDTO userDTO, Principal principal);

    User getCurrentUser(Principal principal);

    UserEntity getUserByPrincipal(Principal principal);

}
