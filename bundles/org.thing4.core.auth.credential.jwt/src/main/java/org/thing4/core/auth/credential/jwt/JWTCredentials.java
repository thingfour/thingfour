package org.thing4.core.auth.credential.jwt;

import org.thing4.core.auth.Credentials;

public class JWTCredentials implements Credentials {

  private static final String SCHEME = "Bearer";
  private final String token;

  public JWTCredentials(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  @Override
  public String getScheme() {
    return SCHEME;
  }

}
