package org.thing4.core.auth.provider.apitoken.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhab.core.auth.Authentication;
import org.openhab.core.auth.AuthenticationException;
import org.openhab.core.auth.UserApiTokenCredentials;
import org.openhab.core.auth.UserRegistry;
import org.thing4.core.auth.credential.apitoken.ApiTokenCredentials;

@ExtendWith(MockitoExtension.class)
class ApitokenAuthenticationProviderTest {

  public static final String TOKEN_X = "X";
  public static final String TOKEN_Y = "Y";
  public static final String TOKEN_Z = "Z";


  @Mock
  private UserRegistry registry;

  private ApitokenAuthenticationProvider provider;

  @BeforeEach
  public void setup() throws Exception {
    provider = new ApitokenAuthenticationProvider(registry);
  }

  @Test
  public void testApiTokens() throws Exception {
    stubCall("foo", TOKEN_X, "admin");
    stubCall("bar", TOKEN_Y, "admin");
    stubCall("baz", TOKEN_Z, "admin");

    Assertions.assertThat(provider.authenticate(new ApiTokenCredentials(TOKEN_X)));
    Assertions.assertThat(provider.authenticate(new ApiTokenCredentials(TOKEN_Y)));
    Assertions.assertThat(provider.authenticate(new ApiTokenCredentials(TOKEN_Z)));
  }

  @Test
  public void testInvalidTokens() throws Exception {
    when(registry.authenticate(any(UserApiTokenCredentials.class))).thenThrow(new AuthenticationException("Fail!"));

    Assertions.assertThatThrownBy(() -> provider.authenticate(new ApiTokenCredentials("asdf")))
      .isInstanceOf(AuthenticationException.class);
    Assertions.assertThatThrownBy(() -> provider.authenticate(new ApiTokenCredentials("bcdf")))
      .isInstanceOf(AuthenticationException.class);

    verify(registry).authenticate(argThat(new ApiTokenMatcher("asdf")));
    verify(registry).authenticate(argThat(new ApiTokenMatcher("bcdf")));
  }

  private void stubCall(String username, String token, String scope) throws Exception {
    when(registry.authenticate(argThat(new ApiTokenMatcher(token))))
      .thenReturn(new Authentication(username, scope));
  }

  static class ApiTokenMatcher implements ArgumentMatcher<UserApiTokenCredentials> {

    private final String token;

    ApiTokenMatcher(String token) {
      this.token = token;
    }

    @Override
    public boolean matches(UserApiTokenCredentials creds) {
      return creds != null && token.equals(creds.getApiToken());
    }

    @Override
    public String toString() {
      return "UserApiToken[" + token + "]";
    }
  }

}