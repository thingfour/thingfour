package org.thing4.core.ui.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationBase;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsName;

import javax.ws.rs.core.Application;

@Component(service = Application.class, property = {
    "servlet.init.hide-service-list-page=false" })
@JaxrsName(Thing4RestConstants.JAX_RS_NAME)
@JaxrsApplicationBase(Thing4Application.THING4_APPLICATION_BASE)
public class Thing4Application extends Application {

  public static final String THING4_APPLICATION_BASE = "thing4";

}
