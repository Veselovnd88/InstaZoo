package ru.veselov.instazoo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Comment implements Serializable {

    private Long id;

    private String username;

    private Long userId;

    private Long postId;

    private String message;

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime createdAt;

}