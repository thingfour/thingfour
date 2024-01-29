package org.thing4.core.auth.credential.apitoken;

import org.thing4.core.auth.Credentials;

/**
 * Credentials which represent openHAB user API token.
 */
public class ApiTokenCredentials implements Credentials {

  private static final String API_TOKEN = "apitoken";

  private final String userApiToken;

  /**
   * Creates a new instance
   *
   * @param userApiToken the user API token
   */
  public ApiTokenCredentials(String userApiToken) {
    this.userApiToken = userApiToken;
  }

  /**
   * Retrieves the user API token
   *
   * @return the token
   */
  public String getApiToken() {
    return userApiToken;
  }

  @Override
  public String getScheme() {
    return API_TOKEN;
  }
}
