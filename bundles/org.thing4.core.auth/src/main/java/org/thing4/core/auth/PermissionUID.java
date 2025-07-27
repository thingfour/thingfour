package org.thing4.core.auth;

import org.openhab.core.common.AbstractUID;

/**
 * Permission identifier formatted as "permission:<source>:<value>".
 *
 * The value part format is not specified, it can be anything as long as it matches UID format.
 */
public class PermissionUID extends AbstractUID {

  public static String TYPE = "permission";

  @Override
  protected int getMinimalNumberOfSegments() {
    return 3;
  }

}
