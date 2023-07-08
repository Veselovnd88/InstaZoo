package ru.veselov.instazoo.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {

    @Schema(description = "Refresh token", example = "")
    @NotEmpty(message = "Refresh token cannot be null")
    private String refreshToken;

}