package ru.veselov.instazoo.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.veselov.instazoo.exception.error.ErrorConstants;
import ru.veselov.instazoo.exception.error.ErrorResponse;

import javax.naming.AuthenticationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    public ErrorResponse<String> handleEntityAlreadyExistsException(RuntimeException exception) {
        return new ErrorResponse<>(ErrorConstants.ERROR_CONFLICT, exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomValidationException.class)
    @ResponseBody
    public ErrorResponse<Map<String, String>> handleValidationException(CustomValidationException exception) {
        return new ErrorResponse<>(
                ErrorConstants.ERROR_VALIDATION,
                exception.getValidationMap(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    public ErrorResponse<String> handleEntityNotFoundException(RuntimeException exception) {
        return new ErrorResponse<>(ErrorConstants.ERROR_NOT_FOUND, exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ErrorResponse<String> handleAuthenticationException(RuntimeException exception) {
        return new ErrorResponse<>(ErrorConstants.ERROR_NOT_FOUND, exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ImageProcessingException.class)
    @ResponseBody
    public ErrorResponse<String> handleImageProcessingException(RuntimeException exception) {
        return new ErrorResponse<>(ErrorConstants.SERVER_ERROR, exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

}