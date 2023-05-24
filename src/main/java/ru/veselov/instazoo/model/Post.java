package ru.veselov.instazoo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class Post implements Serializable {

    private Long id;

    private String title;

    private String caption;

    private String location;

    private Integer likes;

    private String username;

    private Set<String> likedUsers = new HashSet<>();

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime createdAt;

}