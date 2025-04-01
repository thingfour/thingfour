package org.thing4.core.auth.internal;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.openhab.core.auth.Authentication;
import org.openhab.core.auth.AuthenticationException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thing4.core.auth.*;
import org.thing4.core.auth.permission.registry.PermissionRegistry;

@Component(service = AuthenticationManager.class)
public class CompositeAuthenticationManager implements AuthenticationManager {

  private final Logger logger = LoggerFactory.getLogger(CompositeAuthenticationManager.class);
  private final Set<AuthenticationProvider> providers = new CopyOnWriteArraySet<>();
  private final org.openhab.core.auth.AuthenticationManager manager;
  private final PermissionRegistry permissionRegistry;


  @Activate
  public CompositeAuthenticationManager(@Reference org.openhab.core.auth.AuthenticationManager manager, @Reference PermissionRegistry permissionRegistry) {
    this.manager = manager;
    this.permissionRegistry = permissionRegistry;
  }

  @Override
  public AuthenticationResult authenticate(Credentials credentials) throws AuthenticationException {
    boolean unmatched = true;
    for (AuthenticationProvider provider : providers) {
      if (provider.supports(credentials.getClass())) {
        unmatched = false;
        try {
          AuthenticationResult result = provider.authenticate(credentials);
          if (result != null) {
            return result;
          }
        } catch (AuthenticationException e) {
          logger.info("Failed to authenticate credentials {} with provider {}", credentials.getClass(), provider, e);
        }
      }
    }

    // delegate call to openHAB auth manager
    Authentication authenticate = manager.authenticate(credentials);
    permissionRegistry.get(new PrincipalUID(credentials.getScheme(), authenticate.getUsername()));
    return new AuthenticationResult(new PrincipalWrapper(authenticate.getUsername()), credentials.getScheme(), authenticate);
  }

  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  public void addAuthenticationProvider(AuthenticationProvider provider) {
    providers.add(provider);
  }

  public void removeAuthenticationProvider(AuthenticationProvider provider) {
    providers.remove(provider);
  }
}
