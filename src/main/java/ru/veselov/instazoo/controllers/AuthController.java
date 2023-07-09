package ru.veselov.instazoo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.instazoo.payload.request.LoginRequest;
import ru.veselov.instazoo.payload.request.RefreshTokenRequest;
import ru.veselov.instazoo.payload.request.SignUpRequest;
import ru.veselov.instazoo.payload.response.AuthResponse;
import ru.veselov.instazoo.payload.response.ResponseMessage;
import ru.veselov.instazoo.service.AuthenticationService;
import ru.veselov.instazoo.service.RefreshTokenService;
import ru.veselov.instazoo.service.UserService;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication controller", description = "API for registration and signing in")
public class AuthController {

    private final AuthenticationService authenticationService;

    private final FieldErrorResponseService fieldErrorResponseService;

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "Sign in with your login and password", description = "Return response with Jwt")
    @ApiResponse(responseCode = "202", description = "Successfully authenticated",
            content = @Content(schema = @Schema(implementation = AuthResponse.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthResponse authenticateUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(content =
    @Content(schema = @Schema(implementation = LoginRequest.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
                                         @Valid @RequestBody LoginRequest login, BindingResult result) {
        fieldErrorResponseService.validateFields(result);
        return authenticationService.authenticate(login);
    }

    @Operation(summary = "Registration of user", description = "Register user and confirmation message")
    @ApiResponse(responseCode = "201", description = "Successfully registered",
            content = @Content(schema = @Schema(implementation = ResponseMessage.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseMessage registerUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(content =
    @Content(schema = @Schema(implementation = SignUpRequest.class),
            mediaType = MediaType.APPLICATION_JSON_VALUE
    ))
                                        @Valid @RequestBody SignUpRequest signUp, BindingResult result) {
        fieldErrorResponseService.validateFields(result);
        userService.createUser(signUp);
        return new ResponseMessage("User successfully registered");
    }

    @Operation(summary = "Refresh access token after sending refresh token", description = "Return updated tokens")
    @ApiResponse(responseCode = "202", description = "Successfully accepted", content =
    @Content(schema = @Schema(implementation = AuthResponse.class),
            mediaType = MediaType.APPLICATION_JSON_VALUE))
    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthResponse refreshToken(@io.swagger.v3.oas.annotations.parameters.RequestBody(content =
    @Content(schema = @Schema(implementation = RefreshTokenRequest.class),
            mediaType = MediaType.APPLICATION_JSON_VALUE
    ))
                                     @Valid @RequestBody RefreshTokenRequest refreshToken, BindingResult result) {
        fieldErrorResponseService.validateFields(result);
        return refreshTokenService.processRefreshToken(refreshToken.getRefreshToken());
    }

}
