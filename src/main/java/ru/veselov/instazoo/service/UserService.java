package ru.veselov.instazoo.service;

import ru.veselov.instazoo.dto.UserDTO;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.payload.request.SignUpRequest;

import java.security.Principal;

public interface UserService {

    void createUser(SignUpRequest signUp);

    User updateUser(UserDTO userDTO, Principal principal);

    User getCurrentUser(Principal principal);

}