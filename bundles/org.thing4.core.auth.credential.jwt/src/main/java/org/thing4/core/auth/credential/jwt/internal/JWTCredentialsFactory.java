package org.thing4.core.auth.credential.jwt.internal;

import java.util.Optional;
import org.osgi.service.component.annotations.Component;
import org.thing4.core.auth.CredentialFactory;
import org.thing4.core.auth.credential.jwt.JWTCredentials;

@Component(property = {
    "alias=jwt"
})
public class JWTCredentialsFactory implements CredentialFactory<String, JWTCredentials> {

  @Override
  public boolean supports(Class<?> inputType) {
    return inputType.isAssignableFrom(String.class);
  }

  @Override
  public Optional<JWTCredentials> create(String input) {
    if (input.matches("^.+\\..+\\..+$")) {
      return Optional.of(new JWTCredentials(input));
    }
    return Optional.empty();
  }
}
