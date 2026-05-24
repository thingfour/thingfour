package org.thing4.core.auth.credential.cookie;

import org.openhab.core.auth.Credentials;

/**
 * Credentials which represent key/value pair coming from HTTP cookie.
 */
public class CookieCredentials implements Credentials {

  private final String name;
  private final String value;

  public CookieCredentials(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

}
