package org.thing4.core.auth.credential.apitoken.internal;

import java.util.Optional;
import org.osgi.service.component.annotations.Component;
import org.thing4.core.auth.CredentialFactory;
import org.thing4.core.auth.credential.apitoken.ApiTokenCredentials;

@Component(property = {
    "alias=apitoken"
})
public class ApiTokenCredentialFactory implements CredentialFactory<String, ApiTokenCredentials> {

  @Override
  public boolean supports(Class<?> inputType) {
    return inputType.isAssignableFrom(String.class);
  }

  @Override
  public Optional<ApiTokenCredentials> create(String token) {
    return Optional.of(new ApiTokenCredentials(token));
  }

}
