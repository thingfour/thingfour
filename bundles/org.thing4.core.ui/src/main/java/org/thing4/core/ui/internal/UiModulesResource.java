package org.thing4.core.ui.internal;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JSONRequired;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsName;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.thing4.core.ui.UiModule;
import org.thing4.core.ui.UiModuleRegistry;
import org.thing4.core.ui.UiPluginRegistry;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component(service = {Thing4RestResource.class, UiModulesResource.class})
@JaxrsResource
@JSONRequired
@JaxrsName(UiModulesResource.PATH_UI)
@JaxrsApplicationSelect("(" + JaxrsWhiteboardConstants.JAX_RS_NAME + "=" + Thing4RestConstants.JAX_RS_NAME + ")")
@Path(UiModulesResource.PATH_UI)
public class UiModulesResource implements Thing4RestResource {

  public static final String PATH_UI = "/ui";
  public static final String PATH_UI_MODULES = "/ui/modules";

  private final UiModuleRegistry moduleRegistry;
  private final UiPluginRegistry pluginRegistry;

  @Activate
  public UiModulesResource(@Reference UiModuleRegistry moduleRegistry, @Reference UiPluginRegistry pluginRegistry) {
    this.moduleRegistry = moduleRegistry;
    this.pluginRegistry = pluginRegistry;
  }

  @GET
  public String get() {
    return "it is working";
  }

  @GET
  @Path("/plugins")
  public UiPluginsDTO plugins() {
    return new UiPluginsDTO(pluginRegistry.stream()
      .map(plugin -> new UiPluginDTO(plugin.getExtensionPointId(), plugin.getModule(), plugin.getPluginId()))
      .collect(Collectors.toList())
    );
  }


  @GET
  @Path("/{extensionPointId}/plugins.json")
  public UiPluginsDTO extensionPointPlugins(@PathParam("extensionPointId") String extensionPointId) {
    return new UiPluginsDTO(pluginRegistry.stream()
      .filter(plugin -> plugin.getExtensionPointId().equals(extensionPointId))
      .map(plugin -> new UiPluginDTO(plugin.getExtensionPointId(), plugin.getModule(), plugin.getPluginId()))
      .collect(Collectors.toList())
    );
  }

  @GET
  @Path("/modules.json")
  public UiModulesDTO modules() {
    List<UiModuleDTO> modules = new ArrayList<>();
    String prefix = "/" + Thing4Application.THING4_APPLICATION_BASE + PATH_UI_MODULES;
//    modules.add(
//      new UiModuleDTO(prefix + "/module1/module.js", "test")
//    );
    moduleRegistry.stream().map(module -> new UiModuleDTO(
        prefix + "/" + module.getPath(),
        module.getModule()))
      .forEach(modules::add);
    return new UiModulesDTO(modules);
  }

  @GET
  @Path("/modules/{path: .*}")
  @Produces("text/javascript")
  public String module(@PathParam("path") String path) {
    Optional<UiModule> uiModule = moduleRegistry.stream()
      .filter(module -> {
        return module.getPath().equals(path) || module.getAssets().contains(path);
      })
      .findFirst();
    if (uiModule.isPresent()) {
      String resolve = uiModule.get().resolve(path);
      if (resolve != null) {
        return resolve;
      }
      throw new WebApplicationException(Response.Status.NOT_ACCEPTABLE);
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @GET
  @Path("/module1/module.js")
  @Produces("text/javascript")
  public String module1() {
    return "\"use strict\";\n" +
      "(function () {\n" +
      "    console.log(\"Remote module1 loaded!\");\n" +
      "    alert(\"Hello from module.js\");\n" +
      "})();\n";
  }

}

class UiPluginsDTO {
  public final List<UiPluginDTO> plugins;
  public UiPluginsDTO(List<UiPluginDTO> plugins) {
    this.plugins = plugins;
  }
}
class UiPluginDTO {

  private final String extensionPointId;
  private final String module;
  private final String pluginId;

  public UiPluginDTO(String extensionPointId, String module, String pluginId) {
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
}

class UiModulesDTO {
  public List<UiModuleDTO> modules;
  public UiModulesDTO(List<UiModuleDTO> modules) {
    this.modules = modules;
  }
}

class UiModuleDTO {
  private final String path;
  private final String module;

  UiModuleDTO(String path, String module) {
    this.path = path;
    this.module = module;
  }

  public String getPath() {
    return path;
  }

  public String getModule() {
    return module;
  }
}