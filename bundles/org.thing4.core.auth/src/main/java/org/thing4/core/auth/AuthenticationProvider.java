package org.thing4.core.auth;

import org.openhab.core.auth.AuthenticationException;

public interface AuthenticationProvider {

  AuthenticationResult authenticate(final Credentials credentials) throws AuthenticationException;

  boolean supports(Class<? extends Credentials> type);

}
