package ru.veselov.instazoo.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.veselov.instazoo.annotations.PasswordMatches;

@Data
@PasswordMatches
public class SignUpRequest {

    @Schema(description = "E-mail", example = "email@email.com")
    @Email(message = "Field should have e-mail format")
    @NotBlank
    private String email;

    @Schema(description = "User's first name", example = "Doggo")
    @NotEmpty(message = "Please enter your name")
    private String firstname;

    @Schema(description = "User's last name", example = "Blacko")
    @NotEmpty(message = "Please enter your lastname")
    private String lastname;

    @Schema(description = "Username", example = "Black Dog")
    @NotEmpty(message = "Please enter your username")
    private String username;

    @Schema(description = "User's password", example = "s3cr3t")
    @NotEmpty(message = "Password is required")
    @Size(min = 6)
    private String password;

    @Schema(description = "Confirmation of password", example = "s3cr3t")
    private String confirmPassword;

}