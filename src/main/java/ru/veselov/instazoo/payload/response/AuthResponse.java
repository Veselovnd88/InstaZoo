package ru.veselov.instazoo.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    @Schema(description = "Status of authentication")
    private boolean success;

    @Schema(description = "Jwt access token", example = "jwt")
    private String token;

    @Schema(description = "Jwt refresh token", example = "jwt")
    private String refreshToken;

}
