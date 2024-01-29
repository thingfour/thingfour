package org.thing4.core.auth.internal;

import java.security.Principal;

public class PrincipalWrapper implements Principal {

  private final String username;

  public PrincipalWrapper(String username) {
    this.username = username;
  }

  @Override
  public String getName() {
    return username;
  }
}
