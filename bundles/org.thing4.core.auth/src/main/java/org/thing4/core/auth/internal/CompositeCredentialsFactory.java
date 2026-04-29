package org.thing4.core.auth.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.thing4.core.auth.CredentialFactory;
import org.thing4.core.auth.Credentials;
import org.thing4.core.auth.CredentialsFactory;

@Component
public class CompositeCredentialsFactory implements CredentialsFactory {

  private final Map<String, CredentialFactory<Object, Credentials>> factoryMap = new ConcurrentHashMap<>();

  public Optional<Credentials> create(String type, Object input) {
    List<Credentials> credentials = new ArrayList<>();
    CredentialFactory<Object, Credentials> factory = factoryMap.get(type);
    if (input != null && factory != null) {
      if (factory.supports(input.getClass())) {
        return factory.create(input);
      }
    }
    return Optional.empty();
  }

  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  public void addCredentialExtractor(CredentialFactory<Object, Credentials> extractor, Map<String, Object> properties) {
    withAlias(properties, (alias) -> factoryMap.put(alias, extractor));
  }

  public void removeCredentialExtractor(CredentialFactory<Object, Credentials> extractor, Map<String, Object> properties) {
    withAlias(properties, factoryMap::remove);
  }

  private void withAlias(Map<String, Object> properties,
      Consumer<String> consumer) {
    if (properties.containsKey("alias")) {
      String alias = ("" + properties.get("alias")).trim();
      if (!alias.isEmpty()) {
        consumer.accept(alias);
      }
    }
  }

}
