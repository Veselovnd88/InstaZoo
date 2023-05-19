package ru.veselov.instazoo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.instazoo.entity.ImageEntity;
import ru.veselov.instazoo.entity.PostEntity;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.exception.ImageNotFoundException;
import ru.veselov.instazoo.exception.ImageProcessingException;
import ru.veselov.instazoo.mapper.ImageMapper;
import ru.veselov.instazoo.model.ImageModel;
import ru.veselov.instazoo.repository.ImageRepository;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.service.ImageService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final UserRepository userRepository;

    private final ImageRepository imageRepository;

    private final ImageMapper imageMapper;

    @Override
    @Transactional
    public ImageModel uploadImageToUser(MultipartFile file, Principal principal) {
        UserEntity userEntity = getUserByPrincipal(principal);
        ImageEntity profileImage = imageRepository.findByUserId(userEntity.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(profileImage)) {
            imageRepository.delete(profileImage);
        }
        ImageEntity imageEntity = new ImageEntity();
        try {
            imageEntity.setImageBytes(compressBytes(file.getBytes()));
        } catch (IOException e) {
            log.error("Error occurred during reading image file");
            throw new ImageProcessingException("Error occurred during reading image file: " + e.getMessage());
        }
        imageEntity.setName(file.getOriginalFilename());
        imageEntity.setUserId(userEntity.getId());
        imageRepository.save(imageEntity);
        log.info("Image set to [user {}] profile", userEntity.getUsername());
        return imageMapper.entityToModel(imageEntity);
    }

    @Override
    @Transactional
    public ImageModel uploadImageToPost(MultipartFile file, Principal principal, Long postId) {
        UserEntity userEntity = getUserByPrincipal(principal);
        PostEntity post = userEntity.getPosts().stream()
                .filter(x -> x.getId().equals(postId)).
                collect(toSinglePostCollector());
        ImageEntity postImage = new ImageEntity();
        postImage.setPostId(post.getId());
        try {
            postImage.setImageBytes(compressBytes(file.getBytes()));
        } catch (IOException e) {
            log.error("Error occurred during reading image file");
            throw new ImageProcessingException("Error occurred during reading image file: " + e.getMessage());
        }
        postImage.setName(file.getOriginalFilename());
        ImageEntity saved = imageRepository.save(postImage);
        log.info("Uploaded image to [post {}]", post.getId());
        return imageMapper.entityToModel(saved);
    }

    @Override
    public ImageModel getImageToUser(Principal principal) {
        UserEntity userEntity = getUserByPrincipal(principal);
        ImageEntity profileImage = imageRepository.findByUserId(userEntity.getId()).orElseThrow(() -> {
                    log.error("No image found for [user {}]", userEntity.getUsername());
                    throw new ImageNotFoundException(
                            String.format("No image found for [user %s]", userEntity.getUsername())
                    );
                }
        );
        ImageModel imageModel = imageMapper.entityToModel(profileImage);
        imageModel.setImageBytes(decompressBytes(profileImage.getImageBytes()));
        log.info("Retrieving image for [user {}]", userEntity.getUsername());
        return imageModel;
    }

    @Override
    public ImageModel getImageToPost(Long postId) {
        ImageEntity postImage = imageRepository.findByPostId(postId).orElseThrow(() -> {
            log.error("No image found for [post {}]", postId);
            throw new ImageNotFoundException(
                    String.format("No image found for [post %s]", postId)
            );
        });
        ImageModel imageModel = imageMapper.entityToModel(postImage);
        imageModel.setImageBytes(decompressBytes(postImage.getImageBytes()));
        log.info("Retrieving image for [post {}]", postId);
        return imageModel;
    }

    private static byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            baos.write(buffer, 0, count);
        }
        try {
            baos.close();
        } catch (IOException e) {
            log.error("Cannot compress file");
            throw new ImageProcessingException("Compression error: " + e.getMessage());
        }
        log.info("Compressed image byte [size {}]", baos.toByteArray().length);
        return baos.toByteArray();
    }

    private static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(data);
                baos.write(buffer, 0, count);
            }
        } catch (DataFormatException e) {
            log.error("Cannot decompress image");
            throw new ImageProcessingException("Decompression error: " + e.getMessage());
        }
        log.info("Image decompressed with [size {}]", baos.toByteArray().length);
        return baos.toByteArray();
    }

    private <T> Collector<T, ?, T> toSinglePostCollector() {
        //this method will return only one post for user
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException("Error occurred during retrieving post");
                    }
                    return list.get(0);
                }
        );
    }

    private UserEntity getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> {
                    log.error("User with such [username {}] not found", username);
                    throw new UsernameNotFoundException(
                            String.format("User with such [username %s] not found", username)
                    );
                }
        );
    }

}