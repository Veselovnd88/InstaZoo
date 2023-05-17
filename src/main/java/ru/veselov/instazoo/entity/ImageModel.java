package ru.veselov.instazoo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minidev.json.annotate.JsonIgnore;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class ImageModel extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Lob
    @Column(columnDefinition = "bytea")
    private byte[] imageBytes;
    @JsonIgnore
    private Long userId;
    @JsonIgnore
    private Long postId;

}
