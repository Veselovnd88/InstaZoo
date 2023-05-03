package ru.veselov.instazoocource.entity;

import jakarta.persistence.PrePersist;
import ru.veselov.instazoocource.entity.enums.ERole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {

    private Long id;

    private String name;

    private String username;

    private String lastname;

    private String email;

    private String bio;
    private String password;

    private Set<ERole> roles = new HashSet<>();

    private List<Post> posts = new ArrayList<>();

    private LocalDateTime createdAt;

    @PrePersist//this annotation will run this method before saving in DB
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
