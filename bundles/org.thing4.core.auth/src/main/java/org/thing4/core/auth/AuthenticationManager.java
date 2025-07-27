package org.thing4.core.auth;

import org.openhab.core.auth.AuthenticationException;

/**
 * Authentication manager wrapper, main entry point for processing of credentials.
 *
 * Note, this AuthenticationManager is distinct from
 * {@link org.openhab.core.auth.AuthenticationManager}. It returns {@link AuthenticationResult}
 * instead of {@link org.openhab.core.auth.Authentication}.
 */
public interface AuthenticationManager {

  /**
   * Attempt to authenticate caller with specified credentials.
   *
   * @param credentials Credentials to authenticate with.
   * @return Authentication result when operation can be completed.
   * @throws AuthenticationException when none of available authentication methods succeeded.
   */
  AuthenticationResult authenticate(Credentials credentials) throws AuthenticationException;

}
