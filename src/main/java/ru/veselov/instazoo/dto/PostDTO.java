package ru.veselov.instazoo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class PostDTO {
    private Long id;

    @NotEmpty(message = "Should not be empty")
    private String title;

    @NotEmpty(message = "Should not be empty")
    private String caption;

    private String location;

    private Integer likes;

    private Set<String> likedUsers;

}
