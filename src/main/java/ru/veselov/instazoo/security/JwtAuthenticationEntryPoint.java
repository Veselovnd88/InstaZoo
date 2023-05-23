package ru.veselov.instazoo.security;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.veselov.instazoo.exception.error.ErrorConstants;
import ru.veselov.instazoo.exception.error.BasicErrorResponse;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("{}", authException.getMessage());
        BasicErrorResponse basicErrorResponse = new BasicErrorResponse(
                ErrorConstants.ERROR_NOT_AUTHORIZED,
                "Invalid login, password or token");
        String jsonLoginResponse = new Gson().toJson(basicErrorResponse);
        response.setContentType(SecurityConstants.CONTENT_TYPE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().println(jsonLoginResponse);
        log.error("Invalid credentials data");
    }

}