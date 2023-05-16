package ru.veselov.instazoocource.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "post")
public class PostEntity extends BaseEntity {
    @Column(name = "title")
    private String title;
    @Column(name = "caption")
    private String caption;
    @Column(name = "location")
    private String location;
    @Column(name = "likes")
    private Integer likes;
    @Column(name = "liked_users")
    @ElementCollection(targetClass = String.class)
    private Set<String> likedUsers = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;
    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, mappedBy = "post", orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();

}
