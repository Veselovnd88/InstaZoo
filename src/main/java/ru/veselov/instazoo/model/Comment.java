package ru.veselov.instazoo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.veselov.instazoo.entity.PostEntity;

import java.time.LocalDateTime;

@Data
public class Comment {

    private Long id;

    private PostEntity post;

    private String username;

    private Long userId;

    private String message;

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime createdAt;

}