package org.thing4.core.grqphql.internal;

import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring.Builder;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.thing4.core.grqphql.Handler;

public class ScalarHandler implements Handler {

  @Override
  public String getSystemId() {
    return "scalar";
  }

  @Override
  public TypeDefinitionRegistry getTypeDefinitionRegistry() {
    SchemaParser parser = new SchemaParser();
    return parser.parse(getClass().getResourceAsStream("/schema/scalars.graphqls"));
  }

  @Override
  public void contributeWiring(Builder builder) {
    builder.scalar(ExtendedScalars.newAliasedScalar("JSON").aliasedScalar(ExtendedScalars.Json).build())
      .scalar(ExtendedScalars.newAliasedScalar("URL").aliasedScalar(ExtendedScalars.Url).build())
      .scalar(ExtendedScalars.UUID)
      .scalar(ExtendedScalars.GraphQLLong);
  }
}
