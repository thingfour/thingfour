package org.thing4.core.io.rest.jackson.internal;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsExtension;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsMediaType;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@Component(scope = PROTOTYPE, service = {MessageBodyReader.class, MessageBodyWriter.class})
@JaxrsExtension
// TODO make app name dynamic
@JaxrsApplicationSelect("(" + JaxrsWhiteboardConstants.JAX_RS_NAME + "=thing4)")
@JaxrsMediaType({ MediaType.APPLICATION_JSON })
public class JsonProvider extends JacksonJsonProvider {


}
