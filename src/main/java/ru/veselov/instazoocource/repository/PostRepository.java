package ru.veselov.instazoocource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veselov.instazoocource.entity.Post;
import ru.veselov.instazoocource.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserOrderByCreatedAtDesc(UserEntity user);

    List<Post> findAllByOrderByCreatedAtDesc();

    Optional<Post> findPostByIdAndUser(Long id, UserEntity user);

}