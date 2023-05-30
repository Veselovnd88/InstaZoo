package ru.veselov.instazoo.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.instazoo.payload.request.LoginRequest;
import ru.veselov.instazoo.payload.request.RefreshTokenRequest;
import ru.veselov.instazoo.payload.request.SignUpRequest;
import ru.veselov.instazoo.payload.response.AuthResponse;
import ru.veselov.instazoo.service.AuthenticationService;
import ru.veselov.instazoo.service.RefreshTokenService;
import ru.veselov.instazoo.service.UserService;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    AuthenticationService authenticationService;

    @Mock
    FieldErrorResponseService fieldErrorResponseService;

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    UserService userService;

    @InjectMocks
    AuthController authController;

    WebTestClient webTestClient;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(authController)
                .build();
    }

    @Test
    void shouldAuthenticateUser() {
        LoginRequest loginRequest = TestUtils.getLoginRequest();
        AuthResponse authResponse = TestUtils.getAuthResponse();
        Mockito.when(authenticationService.authenticate(loginRequest)).thenReturn(authResponse);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "auth/signin").build())
                .bodyValue(loginRequest)
                .exchange().expectStatus().isAccepted()
                .expectBody().jsonPath("$").exists()
                .jsonPath("$.success").isEqualTo("true")
                .jsonPath("$.token").isEqualTo(authResponse.getToken())
                .jsonPath("$.refreshToken").isEqualTo(authResponse.getRefreshToken());

        Mockito.verify(fieldErrorResponseService, Mockito.times(1)).validateFields(ArgumentMatchers.any());
        Mockito.verify(authenticationService, Mockito.times(1)).authenticate(loginRequest);
    }

    @Test
    void shouldRegisterUser() {
        SignUpRequest signUpRequest = TestUtils.getSignUpRequest();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "auth/signup").build())
                .bodyValue(signUpRequest)
                .exchange().expectStatus().isCreated()
                .expectBody().jsonPath("$").exists()
                .jsonPath("$.message").isEqualTo("User successfully registered");

        Mockito.verify(fieldErrorResponseService, Mockito.times(1)).validateFields(ArgumentMatchers.any());
        Mockito.verify(userService, Mockito.times(1)).createUser(signUpRequest);
    }

    @Test
    void shouldReturnRefreshToken() {
        RefreshTokenRequest refreshTokenRequest = TestUtils.getRefreshTokenRequest();
        AuthResponse authResponse = TestUtils.getAuthResponse();
        Mockito.when(refreshTokenService.processRefreshToken(refreshTokenRequest.getRefreshToken()))
                .thenReturn(authResponse);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "auth/refresh-token").build())
                .bodyValue(refreshTokenRequest)
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").exists()
                .jsonPath("$.success").isEqualTo("true")
                .jsonPath("$.token").isEqualTo(authResponse.getToken())
                .jsonPath("$.refreshToken").isEqualTo(authResponse.getRefreshToken());

        Mockito.verify(fieldErrorResponseService, Mockito.times(1)).validateFields(ArgumentMatchers.any());
        Mockito.verify(refreshTokenService, Mockito.times(1))
                .processRefreshToken(refreshTokenRequest.getRefreshToken());
    }

}
