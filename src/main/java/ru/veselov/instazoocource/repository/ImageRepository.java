package ru.veselov.instazoocource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veselov.instazoocource.entity.ImageModel;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ImageModel, Long> {

    Optional<ImageModel> findByUserId(Long userId);

    Optional<ImageModel> findByPostId(Long postId);

}