package ru.veselov.instazoocource.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"post"}, callSuper = false)
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "message", columnDefinition = "text", nullable = false)
    private String message;

}