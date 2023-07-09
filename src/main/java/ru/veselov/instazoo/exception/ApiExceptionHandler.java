package ru.veselov.instazoo.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.veselov.instazoo.exception.error.BasicErrorResponse;
import ru.veselov.instazoo.exception.error.ErrorConstants;
import ru.veselov.instazoo.exception.error.ValidationErrorResponse;

@RestControllerAdvice
@Slf4j
@ApiResponse(responseCode = "400", description = "Validation of fields failed",
        content = @Content(
                schema = @Schema(implementation = ValidationErrorResponse.class),
                mediaType = MediaType.APPLICATION_JSON_VALUE
        ))
@ApiResponse(responseCode = "401", description = "Authentication failed",
        content = @Content(
                schema = @Schema(implementation = BasicErrorResponse.class),
                examples = @ExampleObject(value = """
                        {
                          "error": "ERROR_UNAUTHORIZED",
                          "code": 401,
                          "message": "Something went wrong with authentication"
                        }"""),
                mediaType = MediaType.APPLICATION_JSON_VALUE
        ))
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

    @ExceptionHandler(ImageProcessingException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BasicErrorResponse handleImageProcessingException(RuntimeException exception) {
        return new BasicErrorResponse(ErrorConstants.SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(BadTokenException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BasicErrorResponse handleJwtException(RuntimeException exception) {
        return new BasicErrorResponse(ErrorConstants.BAD_REQUEST, exception.getMessage());
    }

}