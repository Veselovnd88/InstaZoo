package ru.veselov.instazoo.web;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.instazoo.payload.request.LoginRequest;
import ru.veselov.instazoo.payload.request.SignUpRequest;
import ru.veselov.instazoo.service.AuthenticationService;
import ru.veselov.instazoo.service.UserService;
import ru.veselov.instazoo.util.TestUtils;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    AuthenticationService authenticationService;

    @MockBean
    FieldErrorResponseService fieldErrorResponseService;

    @MockBean
    UserService userService;

    @Test
    void shouldAuthenticateUser() {
        LoginRequest loginRequest = TestUtils.getLoginRequest();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/api/auth/signin").build())
                .bodyValue(loginRequest)
                .exchange().expectStatus().isAccepted();

        Mockito.verify(fieldErrorResponseService, Mockito.times(1)).validateFields(ArgumentMatchers.any());
        Mockito.verify(authenticationService, Mockito.times(1)).authenticate(loginRequest);
    }

    @Test
    void shouldRegisterUser() {
        SignUpRequest signUpRequest = TestUtils.getSignUpRequest();
        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/api/auth/signup").build())
                .bodyValue(signUpRequest)
                .exchange().expectStatus().isCreated();

        Mockito.verify(fieldErrorResponseService, Mockito.times(1)).validateFields(ArgumentMatchers.any());
        Mockito.verify(userService, Mockito.times(1)).createUser(signUpRequest);
    }
}