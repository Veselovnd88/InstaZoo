package ru.veselov.instazoo.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequest {

    @Schema(description = "Login: username", example = "Black Dog")
    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @Schema(description = "Password", example = "s3cr3t")
    @NotEmpty(message = "Password cannot be empty")
    private String password;

}
