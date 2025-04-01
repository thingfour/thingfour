package org.thing4.core.ui.persistence.internal;

import org.openhab.core.common.registry.ProviderChangeListener;
import org.osgi.service.component.annotations.Component;
import org.thing4.core.ui.UiPlugin;
import org.thing4.core.ui.UiPluginProvider;

import java.util.Collection;
import java.util.List;

@Component(service = UiPluginProvider.class)
public class PersistenceExtensionProvider implements UiPluginProvider {

  @Override
  public Collection<UiPlugin> getAll() {
    return List.of(
        new UiPlugin(
            "routes", "persistence-manager", "my-plugin-id"
        )
    );
  }

  @Override
  public void addProviderChangeListener(ProviderChangeListener<UiPlugin> listener) {}

  @Override
  public void removeProviderChangeListener(ProviderChangeListener<UiPlugin> listener) {}

}
