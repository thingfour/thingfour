package org.thing4.core.auth.internal.permission.registry;

import org.openhab.core.common.registry.AbstractRegistry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.thing4.core.auth.PrincipalUID;
import org.thing4.core.auth.permission.registry.ManagedPermissionProvider;
import org.thing4.core.auth.permission.registry.PermissionEntry;

@Component
public class DefaultPermissionRegistry extends AbstractRegistry<PermissionEntry, PrincipalUID, ManagedPermissionProvider> {

  @Activate
  public DefaultPermissionRegistry(@Reference ManagedPermissionProvider managedPermissionProvider) {
    super(ManagedPermissionProvider.class);
    setManagedProvider(managedPermissionProvider);
  }

  @Override
  protected void onUpdateElement(PermissionEntry oldElement, PermissionEntry element)
      throws IllegalArgumentException {
    super.onUpdateElement(oldElement, element);
  }
}
