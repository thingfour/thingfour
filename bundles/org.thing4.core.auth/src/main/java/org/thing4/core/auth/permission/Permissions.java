package org.thing4.core.auth.permission;

import java.util.Objects;

/**
 * Common permission kinds.
 */
public interface Permissions {

  Permission ALL = new NamedPermission("*");
  Permission READ = new NamedPermission("read");
  Permission STATE = new NamedPermission("state");
  Permission COMMAND = new NamedPermission("command");
  Permission MANAGE = new NamedPermission("manage");

  Permission PERSISTENCE = new NamedPermission("persistence");
}

