package ru.veselov.instazoo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PostDTO {

    @Schema(description = "Post id", example = "100")
    private Long id;

    @Schema(description = "Post title", example = "Post")
    @NotEmpty(message = "Should not be empty")
    private String title;

    @Schema(description = "Post caption", example = "Post Caption")
    @NotEmpty(message = "Should not be empty")
    private String caption;

    @Schema(description = "Location", example = "Real Location")
    private String location;

    @Schema(description = "Username", example = "Black Dog")
    private String username;

    @Schema(description = "Likes quantity", example = "100500")
    private Integer likes;

}
