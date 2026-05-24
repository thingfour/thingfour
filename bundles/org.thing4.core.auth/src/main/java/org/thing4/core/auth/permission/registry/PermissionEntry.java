package org.thing4.core.auth.permission.registry;

import java.util.Set;
import org.openhab.core.common.registry.Identifiable;
import org.thing4.core.auth.PrincipalUID;
import org.thing4.core.auth.permission.Permission;

public class PermissionEntry implements Identifiable<PrincipalUID> {

  private PrincipalUID id;

  private Set<Permission> permissions;

  @Override
  public PrincipalUID getUID() {
    return id;
  }

  public Set<Permission> getPermissions() {
    return permissions;
  }

}
