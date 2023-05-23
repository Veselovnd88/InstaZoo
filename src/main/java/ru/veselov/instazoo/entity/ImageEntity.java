package ru.veselov.instazoo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minidev.json.annotate.JsonIgnore;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "image")
public class ImageEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column
    private byte[] imageBytes;
    @JsonIgnore
    private Long userId;
    @JsonIgnore
    private Long postId;

}