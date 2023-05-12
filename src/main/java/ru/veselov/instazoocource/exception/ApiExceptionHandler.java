package ru.veselov.instazoocource.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.veselov.instazoocource.exception.error.ErrorConstants;
import ru.veselov.instazoocource.exception.error.ErrorResponse;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    public ErrorResponse handleEntityAlreadyExistsException(RuntimeException exception) {
        return new ErrorResponse(ErrorConstants.ERROR_CONFLICT, exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomValidationException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleValidationException(CustomValidationException exception) {
        return new ResponseEntity<>(exception.getValidationMap(), HttpStatus.BAD_REQUEST);
    }

}