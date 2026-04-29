package org.thing4.core.auth.context.servlet.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openhab.core.auth.AuthenticationException;
import org.thing4.core.auth.AuthenticationManager;
import org.thing4.core.auth.AuthenticationResult;
import org.thing4.core.auth.Credentials;
import org.thing4.core.auth.CredentialsFactory;

// TODO add configuration of authenticated paths
public class ServletAuthenticationFilter implements Filter {

  private final AuthenticationManager authenticationManager;
  private final CredentialsFactory credentialFactory;

  public ServletAuthenticationFilter(AuthenticationManager authenticationManager, CredentialsFactory credentialFactory) {
    this.authenticationManager = authenticationManager;
    this.credentialFactory = credentialFactory;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
    if (!(request instanceof HttpServletRequest)) {
      throw new ServletException("Request is not an instance of HttpServletRequest");
    }

    if (!(response instanceof HttpServletResponse)) {
      throw new ServletException("Response is not an instance of HttpServletResponse");
    }

    List<Credentials> credentials = new ArrayList<>();
    HttpServletRequest servletRequest = (HttpServletRequest) request;
    for (Cookie cookie : servletRequest.getCookies()) {
      credentials.addAll(extract("cookie", cookie.getValue()));
    }
    String token = servletRequest.getHeader("X-OPENHAB-TOKEN");
    if (token != null) {
      credentialFactory.create("apitoken", token).ifPresent(credentials::add);
    }
    String authorization = servletRequest.getHeader("Authorization");
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
          request.setAttribute(AuthenticationResult.class.getName(), authenticationResult);
        }
      } catch (AuthenticationException e) {
        throw new RuntimeException(e);
      }
    }

    chain.doFilter(request, response);
  }

  private List<Credentials> extract(String type, Object value) {
    return credentialFactory.create(type, value)
      .map(Collections::singletonList)
      .orElse(Collections.emptyList());
  }

  @Override
  public void destroy() {

  }
}
