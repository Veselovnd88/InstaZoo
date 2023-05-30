package ru.veselov.instazoo.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth")
@Getter
@Setter
public class AuthProperties {

    private String secret;

    private String prefix;

    private String header;

    private Long expirationTime;

    private Long refreshExpirationTime;

}