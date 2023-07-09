package ru.veselov.instazoo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.instazoo.model.ImageModel;
import ru.veselov.instazoo.payload.response.ResponseMessage;
import ru.veselov.instazoo.service.ImageService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Image controller", description = "API for managing images")
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "Upload user image", description = "Upload user image and return info message")
    @ApiResponse(responseCode = "200", description = "Successfully uploaded",
            content = @Content(schema = @Schema(implementation = ResponseMessage.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseMessage uploadImageToUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = "Multipart file",
            content = @Content(schema = @Schema(type = "object", requiredProperties = {"title", "content"},
                    title = "file", format = "binary"),
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                             @RequestParam("file") MultipartFile multipartFile, Principal principal) {
        imageService.uploadImageToUser(multipartFile, principal);
        return new ResponseMessage("Image upload successfully");
    }

    @PostMapping("/{postId}/upload")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseMessage uploadImageToPost(@Parameter(in = ParameterIn.PATH, description = "Post Id")
                                             @PathVariable("postId") String postId,
                                             @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                     required = true, description = "Multipart file",
                                                     content = @Content(schema = @Schema(type = "object",
                                                             requiredProperties = {"title", "content"},
                                                             title = "file", format = "binary"),
                                                             mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                             @RequestParam("file") MultipartFile multipartFile,
                                             Principal principal) {
        imageService.uploadImageToPost(multipartFile, principal, Long.parseLong(postId));
        return new ResponseMessage("Image upload successfully");
    }

    @Operation(summary = "Get user image", description = "Returns user image model")
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(schema = @Schema(implementation = ImageModel.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping(value = "/profile")
    public ImageModel getProfileImage(Principal principal) {
        return imageService.getImageToUser(principal);
    }

    @Operation(summary = "Get post image", description = "Returns post image model")
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(schema = @Schema(implementation = ImageModel.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping("/post/{postId}")
    public ImageModel getPostImage(@Parameter(in = ParameterIn.PATH, description = "Post Id")
                                   @PathVariable("postId") String postId) {
        return imageService.getImageToPost(Long.parseLong(postId));
    }

}