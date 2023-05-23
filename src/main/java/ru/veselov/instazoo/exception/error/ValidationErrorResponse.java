package ru.veselov.instazoo.exception.error;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ValidationErrorResponse extends ErrorResponse {

    private Map<String, String> validationMessages;

    public ValidationErrorResponse(String error, Map<String, String> map) {
        super(error);
        this.validationMessages = map;
    }

}
