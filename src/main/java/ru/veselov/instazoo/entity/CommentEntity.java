package ru.veselov.instazoo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"post"}, callSuper = false)
@Entity
@Table(name = "comment")
public class CommentEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    private PostEntity post;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "message", columnDefinition = "text", nullable = false)
    private String message;

}