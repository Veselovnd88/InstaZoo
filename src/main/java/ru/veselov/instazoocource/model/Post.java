package ru.veselov.instazoocource.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Post {
    private Long id;

    private String title;

    private String caption;

    private String location;

    private Integer likes;

    private Set<String> likedUsers = new HashSet<>();

    private User user;
    private List<Comment> comments = new ArrayList<>();

}