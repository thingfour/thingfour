package org.thing4.core.auth.provider.jwt.internal;

import java.security.Principal;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;

public class ClaimSetPrincipal implements Principal {

  private final String name;

  public ClaimSetPrincipal(JwtClaims claimsSet) throws MalformedClaimException {
    this(claimsSet.getStringClaimValue("preferred_username"));
  }

  public ClaimSetPrincipal(String username) {
    this.name = username;
  }

  @Override
  public String getName() {
    return this.name;
  }

}
