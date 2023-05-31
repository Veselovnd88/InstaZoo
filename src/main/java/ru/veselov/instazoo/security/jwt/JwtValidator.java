package ru.veselov.instazoo.security.jwt;

public interface JwtValidator {

    boolean validateAccessToken(String token);

    boolean validateRefreshToken(String token);

}
