package ru.veselov.instazoo.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.instazoo.dto.UserDTO;
import ru.veselov.instazoo.mapper.UserMapper;
import ru.veselov.instazoo.mapper.UserMapperImpl;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.service.UserService;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    WebTestClient webTestClient;

    @Mock
    UserService userService;

    @Mock
    FieldErrorResponseService fieldErrorResponseService;

    @InjectMocks
    UserController userController;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(userController, "userMapper", new UserMapperImpl(), UserMapper.class);
        webTestClient = MockMvcWebTestClient.bindToController(userController).build();
    }

    @Test
    void shouldCallUserServiceToGetCurrentUser() {
        User user = TestUtils.getUser();
        Mockito.when(userService.getCurrentUser(ArgumentMatchers.any())).thenReturn(user);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "/user").build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").exists()
                .jsonPath("$.username").isEqualTo(user.getUsername())
                .jsonPath("$.id").isEqualTo(user.getId())
                .jsonPath("$.email").isEqualTo(user.getEmail())
                .jsonPath("$.firstname").isEqualTo(user.getFirstname())
                .jsonPath("$.lastname").isEqualTo(user.getLastname());

        Mockito.verify(userService, Mockito.times(1)).getCurrentUser(ArgumentMatchers.any());
    }

    @Test
    void shouldCallUserServiceToGetUserProfile() {
        String userId = "1";
        User user = TestUtils.getUser();
        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "/user")
                        .path("/" + userId).build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").exists()
                .jsonPath("$.username").isEqualTo(user.getUsername())
                .jsonPath("$.id").isEqualTo(user.getId())
                .jsonPath("$.email").isEqualTo(user.getEmail())
                .jsonPath("$.firstname").isEqualTo(user.getFirstname())
                .jsonPath("$.lastname").isEqualTo(user.getLastname());

        Mockito.verify(userService, Mockito.times(1)).getUserById(1L);
    }

    @Test
    void shouldCallUserServiceToUpdateUser() {
        UserDTO userDTO = TestUtils.getUserDTO();
        User user = TestUtils.getUser();
        Mockito.when(userService.updateUser(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(user);

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "user").path("/update").build())
                .bodyValue(userDTO)
                .exchange().expectStatus().isAccepted()
                .expectBody().jsonPath("$").exists()
                .jsonPath("$.username").isEqualTo(user.getUsername())
                .jsonPath("$.id").isEqualTo(user.getId())
                .jsonPath("$.email").isEqualTo(user.getEmail())
                .jsonPath("$.firstname").isEqualTo(user.getFirstname())
                .jsonPath("$.lastname").isEqualTo(user.getLastname());

        Mockito.verify(fieldErrorResponseService, Mockito.times(1)).validateFields(ArgumentMatchers.any());
        Mockito.verify(userService, Mockito.times(1)).updateUser(
                ArgumentMatchers.any(),
                ArgumentMatchers.any());
    }

}
