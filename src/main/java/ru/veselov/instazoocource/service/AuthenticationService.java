package ru.veselov.instazoocource.service;

import ru.veselov.instazoocource.payload.request.LoginRequest;
import ru.veselov.instazoocource.payload.response.AuthResponseDTO;

public interface AuthenticationService {

    AuthResponseDTO authenticate(LoginRequest login);
}
