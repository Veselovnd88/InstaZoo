package ru.veselov.instazoo.security.authentication;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private Object principal;

    private final String jwt;

    private boolean isAuthenticated;

    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities,
                                  Object principal,
                                  boolean isAuthenticated,
                                  String jwt) {
        super(authorities);
        this.principal = principal;
        this.isAuthenticated = isAuthenticated;
        this.jwt = jwt;
    }

    public JwtAuthenticationToken(String jwt) {
        super(Collections.emptyList());
        this.isAuthenticated = false;
        this.jwt = jwt;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

}
