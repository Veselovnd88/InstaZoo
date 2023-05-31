package ru.veselov.instazoo.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.instazoo.exception.BadTokenException;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.payload.response.AuthResponse;
import ru.veselov.instazoo.security.AuthProperties;
import ru.veselov.instazoo.security.jwt.JwtValidator;
import ru.veselov.instazoo.security.jwt.impl.JwtGeneratorImpl;
import ru.veselov.instazoo.security.jwt.impl.JwtParserImpl;
import ru.veselov.instazoo.service.CustomUserDetailsService;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    JwtGeneratorImpl jwtGenerator;

    @Mock
    JwtValidator jwtValidator;

    @Mock
    CustomUserDetailsService userDetailsService;

    @Mock
    JwtParserImpl jwtParser;

    @InjectMocks
    RefreshTokenServiceImpl refreshTokenService;

    User user;

    String token;

    @BeforeEach
    void init() {
        AuthProperties authProperties = new AuthProperties();
        authProperties.setHeader(Constants.AUTH_HEADER);
        authProperties.setPrefix(Constants.BEARER_PREFIX);
        authProperties.setSecret(Constants.SECRET);
        authProperties.setExpirationTime(Constants.EXPIRATION_TIME);
        authProperties.setExpirationTime(Constants.EXPIRATION_REFRESH_TIME);
        ReflectionTestUtils.setField(
                refreshTokenService,
                "authProperties",
                authProperties,
                AuthProperties.class);
        user = TestUtils.getUser();
        token = "token";
    }

    @Test
    void shouldGenerateAccessAndRefreshTokens() {
        Mockito.when(jwtValidator.validateRefreshToken(token)).thenReturn(true);
        Mockito.when(jwtGenerator.generateToken(ArgumentMatchers.any())).thenReturn("generated");
        Mockito.when(jwtParser.getUserIdFromToken(token)).thenReturn(user.getId());
        Mockito.when(jwtGenerator.generateRefreshToken(ArgumentMatchers.any())).thenReturn("refresh");
        Mockito.when(jwtGenerator.isRefreshTokenExpiredSoon(token)).thenReturn(true);
        Mockito.when(userDetailsService.loadUserById(user.getId())).thenReturn(user);

        AuthResponse authResponse = refreshTokenService.processRefreshToken(token);

        Assertions.assertThat(authResponse.getToken()).isEqualTo("Bearer generated");
        Assertions.assertThat(authResponse.getRefreshToken()).isEqualTo("refresh");
        Mockito.verify(jwtValidator, Mockito.times(1)).validateRefreshToken(token);
        Mockito.verify(jwtGenerator, Mockito.times(1)).isRefreshTokenExpiredSoon(token);
        Mockito.verify(jwtGenerator, Mockito.times(1)).generateToken(ArgumentMatchers.any(Authentication.class));
        Mockito.verify(jwtGenerator, Mockito.times(1)).generateRefreshToken(ArgumentMatchers.any(Authentication.class));
    }

    @Test
    void shouldGenerateOnlyAccessToken() {
        Mockito.when(jwtValidator.validateRefreshToken(token)).thenReturn(true);
        Mockito.when(jwtGenerator.generateToken(ArgumentMatchers.any())).thenReturn("generated");
        Mockito.when(jwtParser.getUserIdFromToken(token)).thenReturn(user.getId());
        Mockito.when(jwtGenerator.isRefreshTokenExpiredSoon(token)).thenReturn(false);
        Mockito.when(userDetailsService.loadUserById(user.getId())).thenReturn(user);

        AuthResponse authResponse = refreshTokenService.processRefreshToken(token);

        Assertions.assertThat(authResponse.getToken()).isEqualTo("Bearer generated");
        Assertions.assertThat(authResponse.getRefreshToken()).isEqualTo(token);
        Mockito.verify(jwtValidator, Mockito.times(1)).validateRefreshToken(token);
        Mockito.verify(jwtGenerator, Mockito.times(1)).isRefreshTokenExpiredSoon(token);
        Mockito.verify(jwtGenerator, Mockito.times(1)).generateToken(ArgumentMatchers.any(Authentication.class));
        Mockito.verify(jwtGenerator, Mockito.never()).generateRefreshToken(ArgumentMatchers.any(Authentication.class));
    }

    @Test
    void shouldThrowBadTokenExceptionIfTokenNotValidated() {
        Mockito.when(jwtValidator.validateRefreshToken(token)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> refreshTokenService.processRefreshToken(token))
                .isInstanceOf(BadTokenException.class);

        Mockito.verify(jwtValidator, Mockito.times(1)).validateRefreshToken(token);
        Mockito.verify(jwtGenerator, Mockito.never()).isRefreshTokenExpiredSoon(token);
        Mockito.verify(jwtGenerator, Mockito.never()).generateToken(ArgumentMatchers.any(Authentication.class));
        Mockito.verify(jwtGenerator, Mockito.never()).generateRefreshToken(ArgumentMatchers.any(Authentication.class));
    }

}
