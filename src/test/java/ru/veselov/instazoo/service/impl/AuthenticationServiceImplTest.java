package ru.veselov.instazoo.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.instazoo.payload.request.LoginRequest;
import ru.veselov.instazoo.payload.response.AuthResponse;
import ru.veselov.instazoo.security.AuthProperties;
import ru.veselov.instazoo.security.jwt.impl.JwtGeneratorImpl;
import ru.veselov.instazoo.util.TestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtGeneratorImpl jwtGenerator;

    @InjectMocks
    AuthenticationServiceImpl authenticationService;

    @Captor
    ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor;


    @BeforeEach
    void init() {
        AuthProperties authProperties = TestUtils.getSecurityProperties();
        ReflectionTestUtils.setField(
                authenticationService,
                "authProperties",
                authProperties,
                AuthProperties.class);
    }

    @Test
    void shouldAuthenticateAndReturnResponse() {
        LoginRequest loginRequest = TestUtils.getLoginRequest();
        when(jwtGenerator.generateToken(ArgumentMatchers.any())).thenReturn("jwt");

        AuthResponse authResponse = authenticationService.authenticate(loginRequest);

        verify(authenticationManager, times(1)).authenticate(tokenCaptor.capture());
        UsernamePasswordAuthenticationToken captured = tokenCaptor.getValue();
        verify(jwtGenerator, times(1)).generateToken(ArgumentMatchers.any());
        Assertions.assertThat(captured.getCredentials()).isEqualTo(loginRequest.getPassword());
        Assertions.assertThat(captured.getPrincipal()).isEqualTo(loginRequest.getUsername());
        Assertions.assertThat(authResponse.getToken()).isEqualTo("Bearer jwt");
    }

}