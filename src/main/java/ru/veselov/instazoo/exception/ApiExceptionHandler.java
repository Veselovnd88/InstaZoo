package ru.veselov.instazoo.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import ru.veselov.instazoo.exception.error.BasicErrorResponse;
import ru.veselov.instazoo.exception.error.ErrorConstants;
import ru.veselov.instazoo.exception.error.JwtErrorResponse;
import ru.veselov.instazoo.exception.error.ValidationErrorResponse;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public BasicErrorResponse handleEntityAlreadyExistsException(RuntimeException exception) {
        return new BasicErrorResponse(ErrorConstants.ERROR_CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(CustomValidationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidationException(CustomValidationException exception) {
        return new ValidationErrorResponse(ErrorConstants.ERROR_VALIDATION,
                exception.getValidationMap());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BasicErrorResponse handleEntityNotFoundException(RuntimeException exception) {
        return new BasicErrorResponse(ErrorConstants.ERROR_NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JwtErrorResponse handleJwtExpiredException(RuntimeException exception) {
        return new JwtErrorResponse(ErrorConstants.JWT_EXPIRED, exception.getMessage(), "/api/auth/signin");
    }

    @ExceptionHandler(ImageProcessingException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BasicErrorResponse handleImageProcessingException(RuntimeException exception) {
        return new BasicErrorResponse(ErrorConstants.SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BasicErrorResponse handleMultipartException(RuntimeException exception) {
        return new BasicErrorResponse(ErrorConstants.BAD_REQUEST, exception.getMessage());
    }

}