package ru.veselov.instazoo.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.instazoo.payload.request.LoginRequest;
import ru.veselov.instazoo.payload.response.AuthResponse;
import ru.veselov.instazoo.security.JwtProvider;
import ru.veselov.instazoo.security.SecurityProperties;
import ru.veselov.instazoo.util.Constants;
import ru.veselov.instazoo.util.TestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtProvider jwtProvider;

    @InjectMocks
    AuthenticationServiceImpl authenticationService;

    @Captor
    ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor;


    @BeforeEach
    void init() {
        SecurityProperties securityProperties = new SecurityProperties();
        securityProperties.setHeader(Constants.AUTH_HEADER);
        securityProperties.setPrefix(Constants.BEARER_PREFIX);
        securityProperties.setSecret(Constants.SECRET);
        securityProperties.setExpirationTime(Constants.EXPIRATION_TIME);
        ReflectionTestUtils.setField(
                authenticationService,
                "securityProperties",
                securityProperties,
                SecurityProperties.class);
    }

    @Test
    void shouldAuthenticateAndReturnResponse() {
        LoginRequest loginRequest = TestUtils.getLoginRequest();
        when(jwtProvider.generateToken(ArgumentMatchers.any())).thenReturn("jwt");

        AuthResponse authResponse = authenticationService.authenticate(loginRequest);

        verify(authenticationManager, times(1)).authenticate(tokenCaptor.capture());
        UsernamePasswordAuthenticationToken captured = tokenCaptor.getValue();
        verify(jwtProvider, times(1)).generateToken(ArgumentMatchers.any());
        Assertions.assertThat(captured.getCredentials()).isEqualTo(loginRequest.getPassword());
        Assertions.assertThat(captured.getPrincipal()).isEqualTo(loginRequest.getUsername());
        Assertions.assertThat(authResponse.getToken()).isEqualTo("Bearer jwt");
    }

}