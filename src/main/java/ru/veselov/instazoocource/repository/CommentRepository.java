package ru.veselov.instazoocource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veselov.instazoocource.entity.Comment;
import ru.veselov.instazoocource.entity.Post;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost(Post post);

    List<Comment> findByIdAndUserId(Long id, Long userId);

}