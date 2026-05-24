package org.thing4.core.auth;

import java.util.Optional;

/**
 * Creates credentials of given type from specified input.
 *
 * @param <I> Input type.
 */
public interface CredentialFactory<I, C extends Credentials> {

  boolean supports(Class<?> inputType);

  Optional<C> create(I input);

}
