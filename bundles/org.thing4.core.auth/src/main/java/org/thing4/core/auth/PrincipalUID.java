package org.thing4.core.auth;

import org.openhab.core.common.AbstractUID;

/**
 * The principal UID in format "principal:$scheme:$id".
 * The $id part might be for example username (for password) authentication, API token identifier or
 * JWT principal reference (the value of 'sub' claim).
 */
public class PrincipalUID extends AbstractUID {

  public static final String TYPE = "principal";

  public PrincipalUID() {
    super();
  }

  public PrincipalUID(String id) {
    super(id);
  }

  public PrincipalUID(String scheme, String id) {
    super(TYPE, scheme, id);
  }

  @Override
  protected int getMinimalNumberOfSegments() {
    return 3;
  }

}
