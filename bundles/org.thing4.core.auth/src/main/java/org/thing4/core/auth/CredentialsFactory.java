package org.thing4.core.auth;

import java.util.Optional;

/**
 * Creates credentials from given type.
 */
public interface CredentialsFactory {

  Optional<Credentials> create(String type, Object input);

}
