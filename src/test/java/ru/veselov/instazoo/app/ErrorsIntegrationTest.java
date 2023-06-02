package ru.veselov.instazoo.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.instazoo.app.testcontainers.PostgresTestContainersConfig;
import ru.veselov.instazoo.entity.UserEntity;
import ru.veselov.instazoo.exception.error.ErrorConstants;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.repository.UserRepository;
import ru.veselov.instazoo.security.AuthProperties;
import ru.veselov.instazoo.security.jwt.JwtGenerator;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class ErrorsIntegrationTest extends PostgresTestContainersConfig {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    AuthProperties authProperties;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtGenerator jwtGenerator;

    User user;

    UsernamePasswordAuthenticationToken upAuthToken;

    String jwtHeader;

    @BeforeEach
    void init() {
        user = TestUtils.getUser();
        UserEntity userEntity = TestUtils.getUserEntity();
        UserEntity saved = userRepository.save(userEntity);
        user.setId(saved.getId());
        upAuthToken = new UsernamePasswordAuthenticationToken(user, null);
    }

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnJwtExpiredMessage() {
        authProperties.setExpirationTime(1L);
        jwtHeader = Constants.BEARER_PREFIX + jwtGenerator.generateToken(upAuthToken);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(Constants.PREFIX_URL + "/post").build())
                .header(Constants.AUTH_HEADER, jwtHeader)
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.message").exists()
                .jsonPath("$.signInUrl").exists()
                .jsonPath("$.refreshToken").exists();
    }

    @Test
    void shouldReturnNotAuthorizedError() {
        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "comment")
                        .build())
                .exchange().expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody().jsonPath("$.error").isEqualTo(ErrorConstants.ERROR_NOT_AUTHORIZED);

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "comment")
                        .build())
                .exchange().expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody().jsonPath("$.error").isEqualTo(ErrorConstants.ERROR_NOT_AUTHORIZED);

        webTestClient.delete().uri(uriBuilder -> uriBuilder
                        .path(Constants.PREFIX_URL + "comment")
                        .build())
                .exchange().expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody().jsonPath("$.error").isEqualTo(ErrorConstants.ERROR_NOT_AUTHORIZED);
    }

}
