package ru.veselov.instazoo.util;

import ru.veselov.instazoo.dto.CommentDTO;
import ru.veselov.instazoo.dto.PostDTO;
import ru.veselov.instazoo.dto.UserDTO;
import ru.veselov.instazoo.entity.CommentEntity;
import ru.veselov.instazoo.entity.ImageEntity;
import ru.veselov.instazoo.entity.PostEntity;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.entity.enums.ERole;
import ru.veselov.instazoo.model.Comment;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.payload.request.LoginRequest;
import ru.veselov.instazoo.payload.request.RefreshTokenRequest;
import ru.veselov.instazoo.payload.request.SignUpRequest;
import ru.veselov.instazoo.payload.response.AuthResponse;
import ru.veselov.instazoo.security.AuthProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestUtils {

    public static UserEntity getUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(Constants.USERNAME);
        userEntity.setPassword(Constants.PASSWORD);
        userEntity.setFirstname(Constants.FIRST_NAME);
        userEntity.setLastname(Constants.LAST_NAME);
        userEntity.setRoles(Set.of(ERole.ROLE_USER, ERole.ROLE_ADMIN));
        userEntity.setId(Constants.ANY_ID);
        return userEntity;
    }

    public static SignUpRequest getSignUpRequest() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setFirstname(Constants.FIRST_NAME);
        signUpRequest.setUsername(Constants.USERNAME);
        signUpRequest.setLastname(Constants.LAST_NAME);
        signUpRequest.setEmail(Constants.EMAIL);
        signUpRequest.setPassword(Constants.PASSWORD);
        return signUpRequest;
    }

    public static LoginRequest getLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(Constants.PASSWORD);
        loginRequest.setUsername(Constants.USERNAME);
        return loginRequest;
    }

    public static UserDTO getUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstname("Changed " + Constants.FIRST_NAME);
        userDTO.setUsername(Constants.USERNAME);
        userDTO.setLastname("Changed " + Constants.LAST_NAME);
        userDTO.setId(Constants.ANY_ID);
        userDTO.setBio(Constants.BIO);
        return userDTO;
    }

    public static PostDTO getPostDTO() {
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle(Constants.POST_TITLE);
        postDTO.setCaption(Constants.POST_CAPTION);
        postDTO.setLocation(Constants.POST_LOC);
        return postDTO;
    }

    public static PostEntity getPostEntity() {
        PostEntity postEntity = new PostEntity();
        postEntity.setCaption(Constants.POST_CAPTION);
        postEntity.setLocation(Constants.POST_LOC);
        postEntity.setTitle(Constants.POST_TITLE);
        postEntity.setId(Constants.ANY_ID);
        postEntity.setLikes(100500);
        Set<String> likedUsers = new HashSet<>();
        likedUsers.add("BlackDog");
        likedUsers.add("RedDog");
        postEntity.setLikedUsers(likedUsers);
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setMessage("Best Photo Ever");
        commentEntity.setPost(postEntity);
        postEntity.setComments(List.of(
                commentEntity
        ));
        return postEntity;
    }

    public static CommentDTO getCommentDTO() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setUsername(Constants.USERNAME);
        commentDTO.setMessage(Constants.COMMENT_MESSAGE);
        return commentDTO;
    }

    public static CommentEntity getCommentEntity() {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setUserId(Constants.ANY_ID);
        commentEntity.setPost(getPostEntity());
        commentEntity.setMessage(Constants.COMMENT_MESSAGE);
        commentEntity.setUsername(Constants.USERNAME);
        return commentEntity;
    }

    public static Comment getComment() {
        Comment comment = new Comment();
        comment.setPostId(Constants.ANY_ID);
        comment.setUsername(Constants.USERNAME);
        comment.setMessage(Constants.COMMENT_MESSAGE);
        comment.setUserId(Constants.ANY_ID);
        comment.setId(Constants.ANY_ID);
        return comment;
    }

    public static ImageEntity getImageEntity() {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImageBytes(new byte[]{1, 2, 3, 4});
        imageEntity.setName(Constants.IMAGE_NAME);
        imageEntity.setPostId(Constants.ANY_ID);
        imageEntity.setUserId(Constants.ANY_ID);
        return imageEntity;
    }

    public static User getUser() {
        User user = new User();
        user.setFirstname(Constants.FIRST_NAME);
        user.setLastname(Constants.LAST_NAME);
        user.setEmail(Constants.EMAIL);
        user.setId(Constants.ANY_ID);
        return user;
    }

    public static AuthResponse getAuthResponse() {
        return new AuthResponse(true, "token", "refreshToken");
    }

    public static RefreshTokenRequest getRefreshTokenRequest() {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("refreshToken");
        return refreshTokenRequest;
    }

    public static AuthProperties getSecurityProperties() {
        AuthProperties authProperties = new AuthProperties();
        authProperties.setHeader(Constants.AUTH_HEADER);
        authProperties.setPrefix(Constants.BEARER_PREFIX);
        authProperties.setSecret(Constants.SECRET);
        authProperties.setExpirationTime(Constants.EXPIRATION_TIME);
        return authProperties;
    }
}
