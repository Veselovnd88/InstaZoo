package ru.veselov.instazoocource.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.veselov.instazoocource.entity.enums.ERole;

import java.util.*;

@Entity
@Data
@EqualsAndHashCode(exclude = {"roles", "posts"}, callSuper = false)
@NoArgsConstructor
@Table(name = "zoo_user")
public class User extends BaseEntity implements UserDetails {

    @Column(name = "name", nullable = false)
    private String name;
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

    public User(Long id,
                String username,
                String email,
                String password,
                Collection<? extends GrantedAuthority> authorities) {
        super(id);
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getPassword() {
        return this.password;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}