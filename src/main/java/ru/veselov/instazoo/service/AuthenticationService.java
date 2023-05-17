package ru.veselov.instazoo.service;

import ru.veselov.instazoo.payload.request.LoginRequest;
import ru.veselov.instazoo.payload.response.AuthResponse;

public interface AuthenticationService {

    AuthResponse authenticate(LoginRequest login);
}
