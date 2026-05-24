package org.thing4.core.auth;

import java.security.Principal;
import org.openhab.core.auth.Authentication;

public class AuthenticationResult {

    private final Principal principal;
    private final String scheme;
    private final Authentication authentication;

    public AuthenticationResult(Principal principal, String scheme, Authentication authentication) {
        this.principal = principal;
        this.scheme = scheme;
        this.authentication = authentication;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public String getScheme() {
        return scheme;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

}
