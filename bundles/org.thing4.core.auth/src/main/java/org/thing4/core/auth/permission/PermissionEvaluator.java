package org.thing4.core.auth.permission;

import org.openhab.core.auth.Authentication;
import org.openhab.core.common.registry.Identifiable;

/**
 * Permission evaluator contributes to authorization checks.
 * It works with specific domain object (i.e. Thing which is identifiable) and is able to verify
 * if actual Authentication gives a permission to object in question.
 *
 * @param <K>
 * @param <T>
 */
public interface PermissionEvaluator<K, T extends Identifiable<K>> {

  boolean supports(Class<?> type, Permission permission);

  /**
   * Decide if given value is accessible in given authentication context or not.
   *
   * @param permission Permission to be evaluated.
   * @param authentication Authentication to verify.
   * @param value Entity or resource to check.
   * @return True if user can access given object.
   */
  boolean hasPermission(Permission permission, Authentication authentication, T value);

  // same as above but uses type + id instead of entire object
  boolean hasPermission(Permission permission, Authentication authentication, Class<T> type, K id);

}
