package org.thing4.core.grqphql;

import graphql.schema.idl.RuntimeWiring.Builder;
import graphql.schema.idl.TypeDefinitionRegistry;
import java.util.Optional;
import org.dataloader.DataLoaderRegistry;

/**
 * Graph integration handler for specific area.
 *
 * Each handler should contribute its own types and wiring. Additionally, handlers may supply their
 * own loader registry.
 */
public interface Handler {

  /**
   * A unique, human-readable identifier for this contributor (e.g. {@code "users"}).
   * Used in logs and error messages; must be stable across restarts.
   */
  String getSystemId();

  /**
   * Returns the SDL type definitions owned by this contributor.
   *
   * <p>The registry merges all contributor registries into one before building
   * the executable schema. The root {@code Query}, {@code Mutation}, and
   * {@code Subscription} types must be extended here ({@code extend type Query})
   * — only the core bundle defines the empty root types.
   */
  TypeDefinitionRegistry getTypeDefinitionRegistry();

  /**
   * Adds data fetchers, scalar implementations, type resolvers, etc. to the provided wiring builder
   * and returns it.
   *
   * <p>Implementations should only touch type names they own to avoid conflicts.
   *
   * @param builder the shared wiring builder passed through every contributor
   */
  void contributeWiring(Builder builder);

  default Optional<DataLoaderRegistry> getDataLoaderRegistry() {
    return Optional.empty();
  }

}
