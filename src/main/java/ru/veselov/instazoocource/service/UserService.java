package ru.veselov.instazoocource.service;

import ru.veselov.instazoocource.entity.UserEntity;
import ru.veselov.instazoocource.payload.request.SignUpRequest;

public interface UserService {

    UserEntity createUser(SignUpRequest signUp);

}
