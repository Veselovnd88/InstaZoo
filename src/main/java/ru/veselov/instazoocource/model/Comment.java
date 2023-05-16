package ru.veselov.instazoocource.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.veselov.instazoocource.entity.BaseEntity;
import ru.veselov.instazoocource.entity.PostEntity;

@Data
@EqualsAndHashCode(exclude = {"post"}, callSuper = false)
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    private PostEntity post;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "message", columnDefinition = "text", nullable = false)
    private String message;

}