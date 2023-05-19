package ru.veselov.instazoo.web;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.veselov.instazoo.dto.UserDTO;
import ru.veselov.instazoo.mapper.UserMapper;
import ru.veselov.instazoo.model.User;
import ru.veselov.instazoo.service.UserService;
import ru.veselov.instazoo.validation.FieldErrorResponseService;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FieldErrorResponseService fieldErrorResponseService;

    private final UserMapper userMapper;

    @GetMapping("/")
    public User getCurrentUser(Principal principal) {
        return userService.getCurrentUser(principal);
    }

    @GetMapping("/{userId}")
    public User getUserProfile(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    @PutMapping("/update")
    public User updateUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult, Principal principal) {
        fieldErrorResponseService.validateFields(bindingResult);
        return userService.updateUser(userDTO, principal);
    }
}
