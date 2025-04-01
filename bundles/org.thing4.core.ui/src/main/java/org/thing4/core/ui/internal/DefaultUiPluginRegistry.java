package org.thing4.core.ui.internal;

import org.openhab.core.common.registry.AbstractRegistry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.thing4.core.ui.*;

@Component(service = UiPluginRegistry.class)
public class DefaultUiPluginRegistry extends AbstractRegistry<UiPlugin, String, UiPluginProvider>
  implements UiPluginRegistry {

  @Activate
  public DefaultUiPluginRegistry() {
    super(UiPluginProvider.class);
  }

}
