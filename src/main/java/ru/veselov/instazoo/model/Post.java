package ru.veselov.instazoo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Post implements Serializable {

    private Long id;

    private String title;

    private String caption;

    private String location;

    private Integer likes;

    private Set<String> likedUsers = new HashSet<>();

    private User user;

    private List<Comment> comments = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime createdAt;

}