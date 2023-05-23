package ru.veselov.instazoo.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class JwtErrorResponse extends ErrorResponse {

    private String message;

    private String signInUrl;

    public JwtErrorResponse(String error, String message, String signInUrl) {
        super(error);
        this.message = message;
        this.signInUrl = signInUrl;
    }

}