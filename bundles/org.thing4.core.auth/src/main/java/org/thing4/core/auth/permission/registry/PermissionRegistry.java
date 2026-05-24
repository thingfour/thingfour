package org.thing4.core.auth.permission.registry;

import org.openhab.core.common.registry.Registry;
import org.thing4.core.auth.PrincipalUID;
import org.thing4.core.auth.permission.registry.PermissionEntry;

public interface PermissionRegistry extends Registry<PermissionEntry, PrincipalUID> {

}
