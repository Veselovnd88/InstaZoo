package ru.veselov.instazoocource.util;

import ru.veselov.instazoocource.dto.UserDTO;
import ru.veselov.instazoocource.entity.UserEntity;
import ru.veselov.instazoocource.entity.enums.ERole;
import ru.veselov.instazoocource.payload.request.LoginRequest;
import ru.veselov.instazoocource.payload.request.SignUpRequest;

import java.util.Set;

public class UserUtils {

    public static UserEntity getUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(Constants.USERNAME);
        userEntity.setPassword(Constants.PASSWORD);
        userEntity.setFirstname(Constants.FIRST_NAME);
        userEntity.setLastname(Constants.LAST_NAME);
        userEntity.setRoles(Set.of(ERole.ROLE_USER, ERole.ROLE_ADMIN));
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
        userDTO.setId(Constants.USER_ID);
        userDTO.setBio(Constants.BIO);
        return userDTO;
    }
}
