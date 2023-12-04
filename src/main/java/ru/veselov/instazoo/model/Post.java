package ru.veselov.instazoo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class Post implements Serializable {
    @Schema(description = "Post id", example = "1")
    private Long id;

    @Schema(description = "Post title", example = "Title")
    private String title;

    @Schema(description = "Post caption", example = "Caption")
    private String caption;

    @Schema(description = "Post location", example = "Moscow")
    private String location;

    @Schema(description = "Likes quantity", example = "100500")
    private Integer likes;

    @Schema(description = "Username of post owner", example = "Back Dog")
    private String username;

    @ArraySchema(schema = @Schema(description = "User that like this post", example = "Corgi"))
    private Set<String> likedUsers = new HashSet<>();

    @Schema(description = "Creation time", example = "2023-12-01 13:11:40")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime createdAt;

}