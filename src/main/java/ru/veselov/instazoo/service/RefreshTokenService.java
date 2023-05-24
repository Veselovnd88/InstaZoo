package ru.veselov.instazoo.service;

import ru.veselov.instazoo.payload.response.AuthResponse;

public interface RefreshTokenService {

    AuthResponse processRefreshToken(String refreshToken);

}