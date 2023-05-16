package ru.veselov.instazoocource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veselov.instazoocource.entity.CommentEntity;
import ru.veselov.instazoocource.entity.PostEntity;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findAllByPost(PostEntity post);

    List<CommentEntity> findByIdAndUserId(Long id, Long userId);

}