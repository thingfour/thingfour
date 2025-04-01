package org.thing4.core.ui.internal;

import org.openhab.core.common.registry.AbstractRegistry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.thing4.core.ui.UiModuleRegistry;
import org.thing4.core.ui.UiModule;
import org.thing4.core.ui.UiModuleProvider;

@Component(service = UiModuleRegistry.class)
public class DefaultUiModuleRegistry extends AbstractRegistry<UiModule, String, UiModuleProvider>
  implements UiModuleRegistry {

  @Activate
  public DefaultUiModuleRegistry() {
    super(UiModuleProvider.class);
  }

}
