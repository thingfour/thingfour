package org.thing4.core.auth.credential.password;

import org.thing4.core.auth.Credentials;

/**
 * Credentials which represent pair - username and password.
 *
 * This is the simplest credential kind.
 */
public class UsernamePasswordCredentials implements Credentials {

  private final String scheme;
  private final String username;
  private final String password;

  /**
   * Creates a new instance with given scheme.
   *
   * @param username name of the user
   * @param password password of the user
   */
  public UsernamePasswordCredentials(String scheme, String username, String password) {
    this.scheme = scheme;
    this.username = username;
    this.password = password;
  }

  /**
   * Retrieves the username
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Retrieves the password
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  @Override
  public String getScheme() {
    return scheme;
  }
}
