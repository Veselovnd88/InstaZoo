package ru.veselov.instazoocource.security;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.veselov.instazoocource.exception.error.ErrorConstants;
import ru.veselov.instazoocource.exception.error.ErrorResponse;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ErrorResponse<String> errorResponse = new ErrorResponse<>(
                ErrorConstants.ERROR_NOT_AUTHORIZED,
                "Invalid login or password",
                HttpStatus.UNAUTHORIZED);
        String jsonLoginResponse = new Gson().toJson(errorResponse);
        response.setContentType(SecurityConstants.CONTENT_TYPE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().println(jsonLoginResponse);
        log.error("Invalid credentials data [{}]", authException.getMessage());
    }

}