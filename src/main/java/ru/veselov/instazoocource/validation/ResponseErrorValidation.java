package ru.veselov.instazoocource.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.veselov.instazoocource.exception.CustomValidationException;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ResponseErrorValidation {

    public void validateFields(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            log.warn("Validation errors occurred [{}]", errorMap);
            throw new CustomValidationException("Wrong fields", errorMap);
        }
    }

}