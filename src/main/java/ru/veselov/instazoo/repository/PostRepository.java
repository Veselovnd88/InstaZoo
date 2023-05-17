package ru.veselov.instazoo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veselov.instazoo.entity.PostEntity;
import ru.veselov.instazoo.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    List<PostEntity> findAllByUserOrderByCreatedAtDesc(UserEntity user);

    List<PostEntity> findAllByOrderByCreatedAtDesc();

    Optional<PostEntity> findPostByIdAndUser(Long id, UserEntity user);

}