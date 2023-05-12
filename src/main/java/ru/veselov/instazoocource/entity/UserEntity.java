package ru.veselov.instazoocource.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.veselov.instazoocource.entity.enums.ERole;

import java.util.*;

@Entity
@Data
@EqualsAndHashCode(exclude = {"roles", "posts"}, callSuper = false)
@NoArgsConstructor
@Table(name = "zoo_user")
public class UserEntity extends BaseEntity {

    @Column(name = "firstname", nullable = false)
    private String firstname;
    @Column(name = "username", unique = true, updatable = false)//uniqiue field, and we cannot update it
    private String username;
    @Column(name = "lastname", nullable = false)
    private String lastname;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "bio", columnDefinition = "text")
    private String bio;
    @Column(name = "password", length = 3000)// for encryption
    private String password;
    @ElementCollection(targetClass = ERole.class)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"))
    private Set<ERole> roles = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

}