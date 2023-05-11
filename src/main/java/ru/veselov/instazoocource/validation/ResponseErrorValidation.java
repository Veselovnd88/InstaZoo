package ru.veselov.instazoocource.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ResponseErrorValidation {

    public ResponseEntity<Object> mapValidationService(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            List<ObjectError> errorList = bindingResult.getAllErrors();
            if (!CollectionUtils.isEmpty(errorList)) {
                for (ObjectError error : errorList) {
                    errorMap.put(error.getCode(), error.getDefaultMessage());
                }
            }
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            log.warn("Validation errors occurred [{}]", errorMap);
            //TODO throw exception here and catch in RestControllerAdvice
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_GATEWAY);
        }
        return null;
    }
}
