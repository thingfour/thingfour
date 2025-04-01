package org.thing4.core.auth.provider.local.internal;

import org.openhab.core.auth.Authentication;
import org.openhab.core.auth.AuthenticationException;
import org.openhab.core.auth.UserRegistry;
import org.thing4.core.auth.AuthenticationProvider;
import org.thing4.core.auth.AuthenticationResult;
import org.thing4.core.auth.Credentials;
import org.thing4.core.auth.credential.password.UsernamePasswordCredentials;
import org.thing4.core.auth.PrincipalWrapper;

public class LocalAuthenticationProvider implements AuthenticationProvider {

  private final UserRegistry userRegistry;

  public LocalAuthenticationProvider(UserRegistry userRegistry) {
    this.userRegistry = userRegistry;
  }

  @Override
  public AuthenticationResult authenticate(Credentials credentials) throws AuthenticationException {
    if (credentials instanceof UsernamePasswordCredentials) {
      UsernamePasswordCredentials userCredentials = (UsernamePasswordCredentials) credentials;
      Authentication authentication = userRegistry.authenticate(new org.openhab.core.auth.UsernamePasswordCredentials(
        userCredentials.getUsername(), userCredentials.getPassword())
      );

      return new AuthenticationResult(new PrincipalWrapper(authentication.getUsername()), credentials.getScheme(), authentication);
    }

    throw new AuthenticationException("Unsupported credentials " + credentials.getScheme());
  }

  @Override
  public boolean supports(Class<? extends Credentials> type) {
    return type.isAssignableFrom(UsernamePasswordCredentials.class);
  }

}
