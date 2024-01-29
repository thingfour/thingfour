package org.thing4.core.auth;

import java.util.Optional;
import org.openhab.core.auth.Authentication;

/**
 * The access layer to an authentication context during execution of an action.
 */
public interface AuthenticationContextHolder {

  Authentication getAuthentication();

  Optional<Authentication> fetchAuthentication();

}
