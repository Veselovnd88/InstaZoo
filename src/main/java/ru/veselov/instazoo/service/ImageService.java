package ru.veselov.instazoo.service;

import org.springframework.web.multipart.MultipartFile;
import ru.veselov.instazoo.model.ImageModel;

import java.security.Principal;

public interface ImageService {

    ImageModel uploadImageToUser(MultipartFile file, Principal principal);

    ImageModel uploadImageToPost(MultipartFile file, Principal principal, Long postId);

    ImageModel getImageToUser(Principal principal);

    ImageModel getImageToPost(Long postId);

}
