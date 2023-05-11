package ru.veselov.instazoocource.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.veselov.instazoocource.annotations.PasswordMatches;
import ru.veselov.instazoocource.annotations.ValidEmail;

@Data
@PasswordMatches
public class SignUpRequest {
    @Email(message = "Field should have e-mail format")
    @NotBlank
    private String email;

    @NotEmpty(message = "Please enter your name")
    private String firstname;

    @NotEmpty(message = "Please enter your lastname")
    private String lastname;

    @NotEmpty(message = "Please enter your username")
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(min = 6)
    private String password;

    private String confirmPassword;

}