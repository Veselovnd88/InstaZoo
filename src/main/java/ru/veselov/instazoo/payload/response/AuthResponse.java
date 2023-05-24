package ru.veselov.instazoo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private boolean success;

    private String token;

    private String refreshToken;

}