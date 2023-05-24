package ru.veselov.instazoo.payload.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotEmpty(message = "Refresh token cannot be null")
    private String refreshToken;

}