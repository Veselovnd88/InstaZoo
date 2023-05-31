package ru.veselov.instazoo.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.instazoo.app.testcontainers.PostgresTestContainersConfig;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.exception.error.ErrorConstants;
import ru.veselov.instazoo.payload.request.LoginRequest;
import ru.veselov.instazoo.payload.request.SignUpRequest;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AuthControllerIntegrationTest extends PostgresTestContainersConfig {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    UserRepository userRepository;

    @Test
    void shouldCreateUser() {
        SignUpRequest signUpRequest = TestUtils.getSignUpRequest();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "auth/signup").build())
                .bodyValue(signUpRequest)
                .exchange().expectStatus().isCreated()
                .expectBody().jsonPath("$").exists()
                .jsonPath("$.message").isEqualTo("User successfully registered");

        userRepository.deleteAll();
    }

    @Test
    void shouldReturnValidationError() {
        SignUpRequest signUpRequest = TestUtils.getSignUpRequest();
        signUpRequest.setEmail("asdf");

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "auth/signup").build())
                .bodyValue(signUpRequest)
                .exchange().expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody().jsonPath("$.error").isEqualTo(ErrorConstants.ERROR_VALIDATION);
    }

    @Test
    void shouldReturnUserAlreadyExistsError() {
        UserEntity userEntity = TestUtils.getUserEntity();
        userRepository.save(userEntity);

        SignUpRequest signUpRequest = TestUtils.getSignUpRequest();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "auth/signup").build())
                .bodyValue(signUpRequest)
                .exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody().jsonPath("$.error").isEqualTo(ErrorConstants.ERROR_CONFLICT);

        userRepository.deleteAll();
    }

    @Test
    void shouldReturnUserAuthResponseWithTokens() {
        LoginRequest loginRequest = TestUtils.getLoginRequest();
        UserEntity userEntity = TestUtils.getUserEntity();
        userRepository.save(userEntity);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "auth/signin").build())
                .bodyValue(loginRequest)
                .exchange().expectStatus().isEqualTo(HttpStatus.ACCEPTED)
                .expectBody().jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.token").isNotEmpty()
                .jsonPath("$.refreshToken").isNotEmpty();

        userRepository.deleteAll();
    }

    @Test
    void shouldReturnUserNotFoundError() {
        LoginRequest loginRequest = TestUtils.getLoginRequest();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "auth/signin").build())
                .bodyValue(loginRequest)
                .exchange().expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody().jsonPath("$.error").isEqualTo(ErrorConstants.ERROR_NOT_AUTHORIZED);
    }

}
