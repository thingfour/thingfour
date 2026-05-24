package org.thing4.core.auth;

import java.security.Principal;

public class NamedPrincipal implements Principal {

  private final String username;

  public NamedPrincipal(String username) {
    this.username = username;
  }

  @Override
  public String getName() {
    return username;
  }
}
