package org.thing4.core.ui.persistence.internal;

import org.openhab.core.common.registry.ProviderChangeListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thing4.core.ui.UiModule;
import org.thing4.core.ui.UiModuleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component(service = UiModuleProvider.class)
public class PersistenceModuleProvider implements UiModuleProvider {

  private final Logger logger = LoggerFactory.getLogger(PersistenceModuleProvider.class);
  private final Bundle bundle;

  @Activate
  public PersistenceModuleProvider(BundleContext context) {
    this.bundle = context.getBundle();
  }

  @Override
  public Collection<UiModule> getAll() {
    return List.of(
      new UiModule("persistence-manager/module.js", "persistence-manager") {
        @Override
        public String resolve(String path) {
          InputStream resource = getClass().getResourceAsStream("/static/" + path);
          if (resource == null) {
            return null;
          }
          try {
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
          } catch (IOException e) {
            logger.debug("Resource could not be resolved {}", path);
            return null;
          }
        }

        @Override
        public Set<String> getAssets() {
          Enumeration<URL> entries = bundle.findEntries("/static", "*.js", true);
          Set<String> paths = new HashSet<>();
          while (entries.hasMoreElements()) {
            URL entry = entries.nextElement();
            paths.add(entry.getPath().substring("/static/".length()));
          }
          return paths;
        }
      }
    );
  }

  @Override
  public void addProviderChangeListener(ProviderChangeListener<UiModule> listener) {
  }

  @Override
  public void removeProviderChangeListener(ProviderChangeListener<UiModule> listener) {

  }

}
