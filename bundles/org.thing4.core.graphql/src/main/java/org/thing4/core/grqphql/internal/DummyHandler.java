package org.thing4.core.grqphql.internal;

import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.TypeName;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.idl.RuntimeWiring.Builder;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import java.util.Map;
import java.util.Random;
import org.thing4.core.grqphql.Handler;

public class DummyHandler implements Handler {

  @Override
  public String getSystemId() {
    return "dummy";
  }

  @Override
  public TypeDefinitionRegistry getTypeDefinitionRegistry() {
    TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
    typeRegistry.add(ObjectTypeDefinition.newObjectTypeDefinition()
        .name("Query")
        .fieldDefinition(
            FieldDefinition.newFieldDefinition().name("dummy").type(new TypeName("Dummy")).build())
        .build()
    );
    typeRegistry.add(ObjectTypeDefinition.newObjectTypeDefinition()
        .name("Dummy")
        .fieldDefinition(FieldDefinition.newFieldDefinition().name("flag").type(new TypeName("Boolean")).build())
        .build()
    );

    return typeRegistry;
  }

  @Override
  public void contributeWiring(Builder builder) {
    TypeRuntimeWiring.Builder queryWiringBuilder = TypeRuntimeWiring.newTypeWiring("Query");
    queryWiringBuilder.dataFetcher("dummy", new DataFetcher() {
      @Override
      public Object get(DataFetchingEnvironment environment) throws Exception {
        return Map.of("flag", new Random().nextBoolean());
      }
    });
    TypeRuntimeWiring queryWiring = queryWiringBuilder.build();
    builder.type(queryWiring);
  }
}
