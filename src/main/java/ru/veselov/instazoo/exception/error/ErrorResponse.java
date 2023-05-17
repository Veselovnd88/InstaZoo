package ru.veselov.instazoo.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ErrorResponse<T> implements Serializable {
    private String error;

    private T message;

    private HttpStatus httpStatus;

}