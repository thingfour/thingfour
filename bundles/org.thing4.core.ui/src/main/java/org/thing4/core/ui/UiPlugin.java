package org.thing4.core.ui;

import org.openhab.core.common.registry.Identifiable;

public class UiPlugin implements Identifiable<String> {

  private final String extensionPointId;
  private final String module;
  private final String pluginId;

  public UiPlugin(String extensionPointId, String module, String pluginId) {
    this.extensionPointId = extensionPointId;
    this.module = module;
    this.pluginId = pluginId;
  }

  public String getExtensionPointId() {
    return extensionPointId;
  }

  public String getModule() {
    return module;
  }

  public String getPluginId() {
    return pluginId;
  }

  @Override
  public String getUID() {
    return extensionPointId + "." + module + "." + pluginId;
  }
}
