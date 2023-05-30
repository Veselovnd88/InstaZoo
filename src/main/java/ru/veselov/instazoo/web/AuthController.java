package ru.veselov.instazoo.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.instazoo.payload.request.LoginRequest;
import ru.veselov.instazoo.payload.request.RefreshTokenRequest;
import ru.veselov.instazoo.payload.request.SignUpRequest;
import ru.veselov.instazoo.payload.response.AuthResponse;
import ru.veselov.instazoo.service.AuthenticationService;
import ru.veselov.instazoo.service.RefreshTokenService;
import ru.veselov.instazoo.service.UserService;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@PreAuthorize("permitAll")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    private final FieldErrorResponseService fieldErrorResponseService;

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthResponse authenticateUser(@Valid @RequestBody LoginRequest login, BindingResult result) {
        fieldErrorResponseService.validateFields(result);
        return authenticationService.authenticate(login);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public String registerUser(@Valid @RequestBody SignUpRequest signUp, BindingResult result) {
        fieldErrorResponseService.validateFields(result);
        userService.createUser(signUp);
        return "User successfully registered";
    }

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshToken, BindingResult result) {
        fieldErrorResponseService.validateFields(result);
        return refreshTokenService.processRefreshToken(refreshToken.getRefreshToken());
    }

}