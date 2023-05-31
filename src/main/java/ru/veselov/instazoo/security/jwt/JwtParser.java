package ru.veselov.instazoo.security.jwt;

public interface JwtParser {

    Long getUserIdFromToken(String token);
}
