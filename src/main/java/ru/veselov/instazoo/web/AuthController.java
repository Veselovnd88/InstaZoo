package ru.veselov.instazoo.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.veselov.instazoo.payload.request.LoginRequest;
import ru.veselov.instazoo.payload.request.RefreshTokenRequest;
import ru.veselov.instazoo.payload.request.SignUpRequest;
import ru.veselov.instazoo.payload.response.AuthResponse;
import ru.veselov.instazoo.payload.response.ResponseMessage;
import ru.veselov.instazoo.service.AuthenticationService;
import ru.veselov.instazoo.service.RefreshTokenService;
import ru.veselov.instazoo.service.UserService;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@PreAuthorize("permitAll")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    private final FieldErrorResponseService fieldErrorResponseService;

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest login, BindingResult result) {
        fieldErrorResponseService.validateFields(result);
        AuthResponse auth = authenticationService.authenticate(login);
        return new ResponseEntity<>(auth, HttpStatus.ACCEPTED);
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpRequest signUp, BindingResult result) {
        fieldErrorResponseService.validateFields(result);
        userService.createUser(signUp);
        return new ResponseEntity<>(new ResponseMessage("User successfully registered"), HttpStatus.CREATED);
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshToken, BindingResult result) {
        fieldErrorResponseService.validateFields(result);
        return refreshTokenService.processRefreshToken(refreshToken.getRefreshToken());
    }

}