package org.thing4.core.ui;

import org.openhab.core.common.registry.Identifiable;

import java.util.Set;

public abstract class UiModule implements Identifiable<String> {

  private final String path;
  private final String module;

  public UiModule(String path, String module) {
    this.path = path;
    this.module = module;
  }

  public String getPath() {
    return path;
  }

  public String getModule() {
    return module;
  }

  @Override
  public String getUID() {
    return path;
  }

  public abstract String resolve(String path);

  public abstract Set<String> getAssets();

}
