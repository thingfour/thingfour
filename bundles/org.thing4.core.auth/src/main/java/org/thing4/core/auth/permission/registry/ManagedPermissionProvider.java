package org.thing4.core.auth.permission.registry;

import org.openhab.core.common.registry.ManagedProvider;
import org.openhab.core.common.registry.Provider;
import org.thing4.core.auth.PrincipalUID;

public interface ManagedPermissionProvider extends ManagedProvider<PermissionEntry, PrincipalUID>,
  Provider<PermissionEntry> {

}
