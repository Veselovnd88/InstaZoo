package ru.veselov.instazoo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserDTO {

    @Schema(description = "User id", example = "100")
    private Long id;

    @Schema(description = "User's firstname", example = "Doggo")
    @NotEmpty
    private String firstname;

    @Schema(description = "User's lastname", example = "Blacko")
    @NotEmpty
    private String lastname;

    @Schema(description = "Username", example = "Black Dog")
    @NotEmpty
    private String username;

    @Schema(description = "Biography information", example = "My Bio")
    private String bio;

    @Schema(description = "Email", example = "email@email.com")
    private String email;

}
