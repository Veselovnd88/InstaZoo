package ru.veselov.instazoo.security;

public interface JwtValidator {

    boolean validateAccessToken(String token);

    boolean validateRefreshToken(String token);

}
