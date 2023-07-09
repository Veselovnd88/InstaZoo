package ru.veselov.instazoo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.instazoo.dto.UserDTO;
import ru.veselov.instazoo.mapper.UserMapper;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.service.UserService;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User controller", description = "API for managing users")
public class UserController {

    private final UserService userService;

    private final FieldErrorResponseService fieldErrorResponseService;

    private final UserMapper userMapper;

    @Operation(summary = "Get current user", description = "Returns current user info")
    @ApiResponse(responseCode = "200", description = "Success", content =
    @Content(schema = @Schema(implementation = UserDTO.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping()
    public UserDTO getCurrentUser(Principal principal) {
        User currentUser = userService.getCurrentUser(principal);
        return userMapper.modelToDTO(currentUser);
    }

    @Operation(summary = "Get user info my Id", description = "Returns requested user's info")
    @ApiResponse(responseCode = "200", description = "Success", content =
    @Content(schema = @Schema(implementation = UserDTO.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping("/{userId}")
    public UserDTO getUserProfile(@Parameter(in = ParameterIn.PATH, description = "User's id", example = "1")
                                  @PathVariable("userId") String userId) {
        User user = userService.getUserById(Long.parseLong(userId));
        return userMapper.modelToDTO(user);
    }

    @Operation(summary = "Update user information", description = "Update user and return updated info")
    @ApiResponse(responseCode = "202", description = "Successfully updated", content =
    @Content(schema = @Schema(implementation = UserDTO.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @PutMapping("/update")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO updateUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(content =
    @Content(schema = @Schema(implementation = UserDTO.class),
            mediaType = MediaType.APPLICATION_JSON_VALUE
    ))
                              @Valid @RequestBody UserDTO userDTO, BindingResult bindingResult, Principal principal) {
        fieldErrorResponseService.validateFields(bindingResult);
        User user = userService.updateUser(userDTO, principal);
        return userMapper.modelToDTO(user);
    }

}
