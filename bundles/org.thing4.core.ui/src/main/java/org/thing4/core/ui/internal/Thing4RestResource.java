package org.thing4.core.ui.internal;

import org.osgi.service.jaxrs.whiteboard.propertytypes.JSONRequired;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface Thing4RestResource {

}
