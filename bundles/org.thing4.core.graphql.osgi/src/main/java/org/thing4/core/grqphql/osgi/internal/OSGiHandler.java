package org.thing4.core.grqphql.osgi.internal;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherResult;
import graphql.schema.idl.RuntimeWiring.Builder;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import org.dataloader.BatchLoader;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.thing4.core.graphql.osgi.model.BundleTO;
import org.thing4.core.grqphql.Handler;

@Component
public class OSGiHandler implements Handler {

  private final DataLoaderRegistry loaderRegistry;

  public OSGiHandler() {
    loaderRegistry = new DataLoaderRegistry();
    loaderRegistry.register("bundle", DataLoaderFactory.newDataLoader(new BatchLoader<Long, BundleTO>() {
      @Override
      public CompletionStage<List<BundleTO>> load(List<Long> keys) {
        System.out.println("Loading " + keys);
        return CompletableFuture.completedFuture(Collections.emptyList());
      }
    }));
  }

  @Override
  public String getSystemId() {
    return "bundles";
  }

  @Override
  public TypeDefinitionRegistry getTypeDefinitionRegistry() {
    SchemaParser parser = new SchemaParser();
    TypeDefinitionRegistry registry = parser.parse(getClass().getResourceAsStream("/schema/bundles.graphqls"));
    return registry;
  }

  @Override
  public void contributeWiring(Builder builder) {
    Bundle bundle = FrameworkUtil.getBundle(getClass());

    builder.type(TypeRuntimeWiring.newTypeWiring("Query", (env) -> {
      env.dataFetcher("bundles", f -> {
        List<BundleTO> bundles = Arrays.stream(bundle.getBundleContext().getBundles())
          .map(b -> BundleTO.builder()
              .setId(b.getBundleId())
              .setName(Optional.ofNullable(b.getHeaders().get("Bundle-Name")).orElse(""))
              .setSymbolicName(b.getSymbolicName())
              .build()
          )
          .collect(Collectors.toList());
        return bundles;
      }).build();
      env.dataFetcher("bundle", f -> {
        Long id = f.getArgument("id");
        Optional<BundleTO> bundleTO = Arrays.stream(bundle.getBundleContext().getBundles())
            .filter(b -> b.getBundleId() == id)
            .map(b -> BundleTO.builder()
                .setId(b.getBundleId())
                .setName(Optional.ofNullable(b.getHeaders().get("Bundle-Name")).orElse(""))
                .setSymbolicName(b.getSymbolicName())
                .build()
            )
            .findFirst();
        if (bundleTO.isEmpty()) {
          GraphQLError error = GraphqlErrorBuilder.newError().message("Bundle not found").build();
          return DataFetcherResult.newResult().error(error).build();
        }
        return DataFetcherResult.newResult().data(bundleTO.get()).build();
      }).build();
      return env;
    }));

  }

  @Override
  public Optional<DataLoaderRegistry> getDataLoaderRegistry() {
    return Optional.of(loaderRegistry);
  }
}
