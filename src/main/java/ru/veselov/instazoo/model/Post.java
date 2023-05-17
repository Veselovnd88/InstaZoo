package ru.veselov.instazoo.model;

import lombok.Data;

import java.io.Serializable;
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

}