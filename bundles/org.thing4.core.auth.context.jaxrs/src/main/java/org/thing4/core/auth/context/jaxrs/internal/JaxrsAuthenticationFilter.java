package org.thing4.core.auth.context.jaxrs.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import org.openhab.core.auth.AuthenticationException;
import org.thing4.core.auth.AuthenticationManager;
import org.thing4.core.auth.AuthenticationResult;
import org.thing4.core.auth.Credentials;
import org.thing4.core.auth.CredentialsFactory;

// TODO add configuration of authenticated paths
public class JaxrsAuthenticationFilter implements ContainerRequestFilter {

  private final AuthenticationManager authenticationManager;
  private final CredentialsFactory credentialFactory;

  public JaxrsAuthenticationFilter(AuthenticationManager authenticationManager, CredentialsFactory credentialFactory) {
    this.authenticationManager = authenticationManager;
    this.credentialFactory = credentialFactory;
  }

  public void filter(ContainerRequestContext requestContext) throws IOException {
    List<Credentials> credentials = new ArrayList<>();
    for (Cookie cookie : requestContext.getCookies().values()) {
      credentials.addAll(extract("cookie", cookie.getValue()));
    }
    String token = requestContext.getHeaderString("X-OPENHAB-TOKEN");
    if (token != null) {
      credentialFactory.create("apitoken", token).ifPresent(credentials::add);
    }
    String authorization = requestContext.getHeaderString("Authorization");
    if (authorization != null) {
      String[] parts = authorization.split(" ");
      if (parts.length > 1) {
        if ("basic".equalsIgnoreCase(parts[0])) {
          String basicData = new String(Base64.getDecoder().decode(parts[1]));
          String[] userAndPassword = basicData.split(":");
          credentials.addAll(extract("password", new String[] {userAndPassword[0], userAndPassword.length > 1 ? userAndPassword[1] : ""}));
        } else if ("bearer".equalsIgnoreCase(parts[0])) {
          // encoding of token is free form, leave it as is
          credentials.addAll(extract("jwt", parts[1]));
        }
      }
    }

    for (Credentials credential : credentials) {
      try {
        AuthenticationResult authenticationResult = authenticationManager.authenticate(credential);
        if (authenticationResult != null) {
          requestContext.setProperty(AuthenticationResult.class.getName(), authenticationResult);
        }
      } catch (AuthenticationException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private List<Credentials> extract(String type, Object value) {
    return credentialFactory.create(type, value)
      .map(Collections::singletonList)
      .orElse(Collections.emptyList());
  }

}
