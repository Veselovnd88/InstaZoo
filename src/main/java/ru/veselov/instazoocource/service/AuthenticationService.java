package ru.veselov.instazoocource.service;

import ru.veselov.instazoocource.payload.request.LoginRequest;
import ru.veselov.instazoocource.payload.response.AuthResponse;

public interface AuthenticationService {

    AuthResponse authenticate(LoginRequest login);
}
