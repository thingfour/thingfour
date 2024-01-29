package org.thing4.core.auth.provider.local.internal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhab.core.auth.Authentication;
import org.openhab.core.auth.AuthenticationException;
import org.openhab.core.auth.UserRegistry;
import org.openhab.core.auth.UsernamePasswordCredentials;
import org.thing4.core.auth.Credentials;

@ExtendWith(MockitoExtension.class)
public class LocalAuthenticationProviderTest {

  public static final String PASSWORD_X = "X";
  public static final String PASSWORD_Y = "Y";
  public static final String PASSWORD_Z = "Z";


  @Mock
  private UserRegistry registry;

  private LocalAuthenticationProvider provider;

  @BeforeEach
  public void setup() throws Exception {
    provider = new LocalAuthenticationProvider(registry);
  }

  @Test
  public void testUsernamePasswordAuth() throws Exception {
    stubCall("foo", PASSWORD_X, "admin");
    stubCall("bar", PASSWORD_Y, "admin");
    stubCall("baz", PASSWORD_Z, "admin");

    Assertions.assertThat(provider.authenticate(credentials("basic", "foo", PASSWORD_X)));
    Assertions.assertThat(provider.authenticate(credentials("basic", "bar", PASSWORD_Y)));
    Assertions.assertThat(provider.authenticate(credentials("basic", "baz", PASSWORD_Z)));
  }

  @Test
  public void testInvalidUsernamePassword() throws Exception {
    when(registry.authenticate(any(UsernamePasswordCredentials.class))).thenThrow(new AuthenticationException("Fail!"));

    Assertions.assertThatThrownBy(() -> provider.authenticate(credentials("basic", "a", "asdf")))
      .isInstanceOf(AuthenticationException.class);
    Assertions.assertThatThrownBy(() -> provider.authenticate(credentials("basic", "a", "bcdf")))
      .isInstanceOf(AuthenticationException.class);

    verify(registry).authenticate(argThat(new UserPasswordMatcher("a", "asdf")));
    verify(registry).authenticate(argThat(new UserPasswordMatcher("a", "bcdf")));
  }

  private Credentials credentials(String scheme, String username, String password) {
    return new org.thing4.core.auth.credential.password.UsernamePasswordCredentials(scheme, username, password);
  }

  private void stubCall(String username, String password, String scope) throws Exception {
    when(registry.authenticate(argThat(new UserPasswordMatcher(username, password))))
      .thenReturn(new Authentication(username, scope));
  }

  static class UserPasswordMatcher implements ArgumentMatcher<UsernamePasswordCredentials> {

    private final String username;
    private final String password;

    UserPasswordMatcher(String username, String password) {
      this.username = username;
      this.password = password;
    }

    @Override
    public boolean matches(UsernamePasswordCredentials creds) {
      return creds != null && username.equals(creds.getUsername()) && password.equals(creds.getPassword());
    }

    @Override
    public String toString() {
      return "UsernamePasswordCredentials[" + username + ", " + password + "]";
    }
  }

}
