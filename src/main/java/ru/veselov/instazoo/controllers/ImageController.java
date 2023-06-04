package ru.veselov.instazoo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.instazoo.model.ImageModel;
import ru.veselov.instazoo.payload.response.ResponseMessage;
import ru.veselov.instazoo.service.ImageService;

import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseMessage uploadImageToUser(@RequestParam("file") MultipartFile multipartFile, Principal principal) {
        imageService.uploadImageToUser(multipartFile, principal);
        return new ResponseMessage("Image upload successfully");
    }

    @PostMapping("/{postId}/upload")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseMessage uploadImageToPost(@PathVariable("postId") String postId,
                                             @RequestParam("file") MultipartFile multipartFile,
                                             Principal principal) {
        imageService.uploadImageToPost(multipartFile, principal, Long.parseLong(postId));
        return new ResponseMessage("Image upload successfully");
    }

    @GetMapping(value = "/profile")
    public ImageModel getProfileImage(Principal principal) {
        return imageService.getImageToUser(principal);
    }

    @GetMapping("/post/{postId}")
    public ImageModel getPostImage(@PathVariable("postId") String postId) {
        return imageService.getImageToPost(Long.parseLong(postId));
    }

}