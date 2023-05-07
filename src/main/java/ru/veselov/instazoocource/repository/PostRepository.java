package ru.veselov.instazoocource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veselov.instazoocource.entity.Post;
import ru.veselov.instazoocource.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserOrderByCreatedAtDateDesc(User user);

    List<Post> findAllByOrderByCreatedAtDateDesc();

    Optional<Post> findPostByIdAndUser(Long id, User user);

}