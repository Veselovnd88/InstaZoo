package ru.veselov.instazoo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veselov.instazoo.entity.CommentEntity;
import ru.veselov.instazoo.entity.PostEntity;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findAllByPost(PostEntity post);

    List<CommentEntity> findByIdAndUserId(Long id, Long userId);

}