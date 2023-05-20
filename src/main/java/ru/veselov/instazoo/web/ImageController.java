package ru.veselov.instazoo.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.instazoo.model.ImageModel;
import ru.veselov.instazoo.service.ImageService;

import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImageToUser(@RequestParam("file") MultipartFile multipartFile, Principal principal) {
        imageService.uploadImageToUser(multipartFile, principal);
        return ResponseEntity.accepted().body("Image upload successfully");
    }

    @PostMapping("/{postId}/upload")
    public ResponseEntity<String> uploadImageToPost(@PathVariable("postId") String postId,
                                                    @RequestParam("file") MultipartFile multipartFile,
                                                    Principal principal) {
        imageService.uploadImageToPost(multipartFile, principal, Long.parseLong(postId));
        return ResponseEntity.accepted().body("Image upload successfully");
    }

    @GetMapping("/profile")
    public ImageModel getProfileImage(Principal principal) {
        return imageService.getImageToUser(principal);
    }

    @GetMapping("/post/{postId}")
    public ImageModel getPostImage(@PathVariable("postId") String postId) {
        return imageService.getImageToPost(Long.parseLong(postId));
    }

}