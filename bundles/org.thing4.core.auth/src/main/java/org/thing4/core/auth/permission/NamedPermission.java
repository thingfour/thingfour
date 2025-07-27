package org.thing4.core.auth.permission;

import java.util.Objects;

class NamedPermission implements Permission {

  private final String code;

  public NamedPermission(String code) {
    this.code = code;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Permission)) {
      return false;
    }
    Permission that = (Permission) o;
    return Objects.equals(getCode(), that.getCode());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCode());
  }

}
