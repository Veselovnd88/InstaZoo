package ru.veselov.instazoocource.service;

import ru.veselov.instazoocource.payload.request.SignUpRequest;

public interface UserService {

    void createUser(SignUpRequest signUp);

}
