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

    private String refreshToken;

    public JwtErrorResponse(String error, String message, String signInUrl, String refreshToken) {
        super(error);
        this.message = message;
        this.signInUrl = signInUrl;
        this.refreshToken = refreshToken;
    }

}