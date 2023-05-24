package ru.veselov.instazoo.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.instazoo.dto.UserDTO;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.security.JwtProvider;
import ru.veselov.instazoo.service.UserService;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

import java.security.Principal;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    JwtProvider jwtProvider;

    @MockBean
    UserService userService;

    @MockBean
    FieldErrorResponseService fieldErrorResponseService;

    @MockBean
    UserRepository userRepository;

    User user;

    String header;

    @BeforeEach
    void init() {
        user = TestUtils.getUser();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, "Pass");
        String jwt = jwtProvider.generateToken(token);
        header = "Bearer " + jwt;
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(TestUtils.getUserEntity()));
    }

    @Test
    void shouldCallUserServiceToGetCurrentUser() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/user").build())
                .headers(headers -> headers.add(Constants.AUTH_HEADER, header))
                .exchange().expectStatus().isOk();

        Mockito.verify(userService, Mockito.times(1)).getCurrentUser(ArgumentMatchers.any(Principal.class));
    }

    @Test
    void shouldCallUserServiceToGetUserProfile() {
        String userId = "1";
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/user").path("/" + userId).build())
                .headers(headers -> headers.add(Constants.AUTH_HEADER, header))
                .exchange().expectStatus().isOk();

        Mockito.verify(userService, Mockito.times(1)).getUserById(1L);
    }

    @Test
    void shouldCallUserServiceToUpdateUser() {
        UserDTO userDTO = TestUtils.getUserDTO();
        webTestClient.put().uri(uriBuilder -> uriBuilder.path("/api/user").path("/update").build())
                .headers(headers -> headers.add(Constants.AUTH_HEADER, header))
                .bodyValue(userDTO)
                .exchange().expectStatus().isAccepted();

        Mockito.verify(fieldErrorResponseService, Mockito.times(1)).validateFields(ArgumentMatchers.any());
        Mockito.verify(userService, Mockito.times(1)).updateUser(
                ArgumentMatchers.any(UserDTO.class),
                ArgumentMatchers.any(Principal.class));
    }

}
