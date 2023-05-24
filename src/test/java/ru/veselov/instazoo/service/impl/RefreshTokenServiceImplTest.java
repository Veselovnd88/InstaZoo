package ru.veselov.instazoo.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
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
import ru.veselov.instazoo.security.JwtProvider;
import ru.veselov.instazoo.security.SecurityProperties;
import ru.veselov.instazoo.security.TokenType;
import ru.veselov.instazoo.service.CustomUserDetailsService;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    JwtProvider jwtProvider;

    @Mock
    CustomUserDetailsService userDetailsService;

    @InjectMocks
    RefreshTokenServiceImpl refreshTokenService;

    User user;

    String token;

    @BeforeEach
    void init() {
        SecurityProperties securityProperties = new SecurityProperties();
        securityProperties.setHeader(Constants.AUTH_HEADER);
        securityProperties.setPrefix(Constants.BEARER_PREFIX);
        securityProperties.setSecret(Constants.SECRET);
        securityProperties.setExpirationTime(Constants.EXPIRATION_TIME);
        securityProperties.setExpirationTime(Constants.EXPIRATION_REFRESH_TIME);
        ReflectionTestUtils.setField(
                refreshTokenService,
                "securityProperties",
                securityProperties,
                SecurityProperties.class);
        user = TestUtils.getUser();
        token = "token";
    }

    @Test
    void shouldGenerateAccessAndRefreshTokens() {
        Mockito.when(jwtProvider.validateToken(token, TokenType.REFRESH)).thenReturn(true);
        Mockito.when(jwtProvider.generateToken(ArgumentMatchers.any())).thenReturn("generated");
        Mockito.when(jwtProvider.getUserIdFromToken(token)).thenReturn(user.getId());
        Mockito.when(jwtProvider.generateRefreshToken(ArgumentMatchers.any())).thenReturn("refresh");
        Mockito.when(jwtProvider.isRefreshTokenExpiredSoon(token)).thenReturn(true);
        Mockito.when(userDetailsService.loadUserById(user.getId())).thenReturn(user);

        AuthResponse authResponse = refreshTokenService.processRefreshToken(token);

        Assertions.assertThat(authResponse.getToken()).isEqualTo("Bearer generated");
        Assertions.assertThat(authResponse.getRefreshToken()).isEqualTo("refresh");
        Mockito.verify(jwtProvider, Mockito.times(1)).validateToken(token, TokenType.REFRESH);
        Mockito.verify(jwtProvider, Mockito.times(1)).isRefreshTokenExpiredSoon(token);
        Mockito.verify(jwtProvider, Mockito.times(1)).generateToken(ArgumentMatchers.any(Authentication.class));
        Mockito.verify(jwtProvider, Mockito.times(1)).generateRefreshToken(ArgumentMatchers.any(Authentication.class));
    }

    @Test
    void shouldGenerateOnlyAccessToken() {
        Mockito.when(jwtProvider.validateToken(token, TokenType.REFRESH)).thenReturn(true);
        Mockito.when(jwtProvider.generateToken(ArgumentMatchers.any())).thenReturn("generated");
        Mockito.when(jwtProvider.getUserIdFromToken(token)).thenReturn(user.getId());
        Mockito.when(jwtProvider.isRefreshTokenExpiredSoon(token)).thenReturn(false);
        Mockito.when(userDetailsService.loadUserById(user.getId())).thenReturn(user);

        AuthResponse authResponse = refreshTokenService.processRefreshToken(token);

        Assertions.assertThat(authResponse.getToken()).isEqualTo("Bearer generated");
        Assertions.assertThat(authResponse.getRefreshToken()).isEqualTo(token);
        Mockito.verify(jwtProvider, Mockito.times(1)).validateToken(token, TokenType.REFRESH);
        Mockito.verify(jwtProvider, Mockito.times(1)).isRefreshTokenExpiredSoon(token);
        Mockito.verify(jwtProvider, Mockito.times(1)).generateToken(ArgumentMatchers.any(Authentication.class));
        Mockito.verify(jwtProvider, Mockito.never()).generateRefreshToken(ArgumentMatchers.any(Authentication.class));
    }

    @Test
    void shouldThrowBadTokenExceptionIfTokenNotValidated() {
        Mockito.when(jwtProvider.validateToken(token, TokenType.REFRESH)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> refreshTokenService.processRefreshToken(token))
                .isInstanceOf(BadTokenException.class);

        Mockito.verify(jwtProvider, Mockito.times(1)).validateToken(token, TokenType.REFRESH);
        Mockito.verify(jwtProvider, Mockito.never()).isRefreshTokenExpiredSoon(token);
        Mockito.verify(jwtProvider, Mockito.never()).generateToken(ArgumentMatchers.any(Authentication.class));
        Mockito.verify(jwtProvider, Mockito.never()).generateRefreshToken(ArgumentMatchers.any(Authentication.class));
    }

    @Test
    void shouldThrowBadTokenExceptionIfTokenIfExpiredExceptionThrown() {
        Mockito.when(jwtProvider.validateToken(token, TokenType.REFRESH)).thenThrow(ExpiredJwtException.class);

        Assertions.assertThatThrownBy(() -> refreshTokenService.processRefreshToken(token))
                .isInstanceOf(BadTokenException.class);

        Mockito.verify(jwtProvider, Mockito.times(1)).validateToken(token, TokenType.REFRESH);
        Mockito.verify(jwtProvider, Mockito.never()).isRefreshTokenExpiredSoon(token);
        Mockito.verify(jwtProvider, Mockito.never()).generateToken(ArgumentMatchers.any(Authentication.class));
        Mockito.verify(jwtProvider, Mockito.never()).generateRefreshToken(ArgumentMatchers.any(Authentication.class));
    }

}
