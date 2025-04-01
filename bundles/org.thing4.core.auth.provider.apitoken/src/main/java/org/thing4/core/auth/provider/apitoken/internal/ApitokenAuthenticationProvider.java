package org.thing4.core.auth.provider.apitoken.internal;

import org.openhab.core.auth.UserApiTokenCredentials;
import org.thing4.core.auth.AuthenticationProvider;
import org.openhab.core.auth.Authentication;
import org.openhab.core.auth.AuthenticationException;
import org.openhab.core.auth.UserRegistry;
import org.thing4.core.auth.AuthenticationResult;
import org.thing4.core.auth.Credentials;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.thing4.core.auth.PrincipalWrapper;
import org.thing4.core.auth.credential.apitoken.ApiTokenCredentials;

/**
 * Authentication provider which is able to correlate received authentication token.
 * In a way it encapsulates a logic which is embedded in openHAB core.
 *
 * @author Łukasz Dywicki
 */
@Component
public class ApitokenAuthenticationProvider implements AuthenticationProvider {

  private final UserRegistry userRegistry;

  @Activate
  public ApitokenAuthenticationProvider(@Reference UserRegistry userRegistry) {
    this.userRegistry = userRegistry;
  }

  @Override
  public AuthenticationResult authenticate(Credentials credentials) throws AuthenticationException {
    UserApiTokenCredentials apiTokenCredentials = new UserApiTokenCredentials(((ApiTokenCredentials) credentials).getApiToken());
    Authentication authentication = userRegistry.authenticate(apiTokenCredentials);
    return new AuthenticationResult(
      new PrincipalWrapper(authentication.getUsername()), credentials.getScheme(), authentication
    );
  }

  @Override
  public boolean supports(Class<? extends Credentials> type) {
    return ApiTokenCredentials.class.isAssignableFrom(type);
  }

}
