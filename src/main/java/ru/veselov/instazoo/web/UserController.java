package ru.veselov.instazoo.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/api/user")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FieldErrorResponseService fieldErrorResponseService;

    private final UserMapper userMapper;

    //FIXME change error when usernotFound
    @GetMapping
    public User getCurrentUser(Principal principal) {
        return userService.getCurrentUser(principal);
    }

    @GetMapping("/{userId}")
    public UserDTO getUserProfile(@PathVariable("userId") String userId) {
        User user = userService.getUserById(Long.parseLong(userId));
        return userMapper.modelToDTO(user);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO updateUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult, Principal principal) {
        fieldErrorResponseService.validateFields(bindingResult);
        User user = userService.updateUser(userDTO, principal);
        return userMapper.modelToDTO(user);
    }

}