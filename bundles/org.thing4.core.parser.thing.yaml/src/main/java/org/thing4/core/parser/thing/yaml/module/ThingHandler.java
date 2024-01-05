package org.thing4.core.parser.thing.yaml.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.List;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.builder.BridgeBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.thing4.core.parser.thing.yaml.module.ThingModule.Channels;
import org.thing4.core.parser.thing.yaml.module.ThingModule.ExtraThingUID;

public class ThingHandler {

  static class Deserializer extends StdDeserializer<Thing> {

    private final InjectableValues.Std injectableValues;

    public Deserializer(InjectableValues.Std injectableValues) {
      super(Thing.class);
      this.injectableValues = injectableValues;
    }

    @Override
    public Thing deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      if (p.getCurrentToken() != JsonToken.START_OBJECT) {
        throw new JsonMappingException(p, "Unexpected call of deserializer.");
      }

      String fieldName = null;
      boolean bridge = false;

      ThingTypeUID thingTypeUID = null;
      String id = null;
      String label = null;
      String location = null;
      ThingUID UID = null;
      ThingUID bridgeUID = null;
      Configuration configuration = null;
      List<Channel> channels = null;

      while ((fieldName = p.nextFieldName()) != null) {
        if ("kind".equals(fieldName)) {
          bridge = "Bridge".equals(p.nextTextValue());
        } else if ("label".equals(fieldName)) {
          label = p.nextTextValue();
        } else if ("location".equals(fieldName)) {
          location = p.nextTextValue();
        } else if ("thingTypeUID".equals(fieldName)) {
          thingTypeUID = ctxt.readValue(p, ThingTypeUID.class);
        } else if ("UID".equals(fieldName)) {
          UID = ctxt.readValue(p, ThingUID.class);
        } else if ("bridgeUID".equals(fieldName)) {
          bridgeUID = ctxt.readValue(p, ThingUID.class);
        } else if ("id".equals(fieldName)) {
          id = p.nextTextValue();
        } else if ("configuration".equals(fieldName)) {
          configuration = ctxt.readValue(p, Configuration.class);
        } else if ("channels".equals(fieldName)) {
          if (UID != null) {
            injectableValues.addValue("thingUID", UID);
            p.setCurrentValue(UID);
          } else if (id != null && thingTypeUID != null) {
            injectableValues.addValue("thingUID", new ThingUID(thingTypeUID, id));
          } else {
            throw new JsonMappingException(p, "Field can not be declared before thing gets proper identification. Move channels field below UID or id/thingTypeUID");
          }
          p.nextToken();
          channels = ctxt.readValue(p, Channels.class);
          injectableValues.addValue("thingUID", null);
        } else {
          throw new JsonMappingException(p, "Unrecognized field: " + fieldName);
        }

      }

      if (UID != null && id != null) {
        throw new JsonMappingException(p, "Invalid mapping detected. UID and id field can't be set at the same time.");
      }
      if (id != null && thingTypeUID == null) {
        throw new JsonMappingException(p, "Invalid mapping detected. When id is set the thingTypeUID must be provided.");
      }

      if (id != null) {
        UID = constructId(thingTypeUID, bridgeUID, id);
      } else {
        thingTypeUID = new ExtraThingUID(UID.getAsString()).getThingType();
      }

      ThingBuilder thingBuilder = null;
      if (bridge) {
        thingBuilder = BridgeBuilder.create(thingTypeUID, UID);
      } else {
        thingBuilder = ThingBuilder.create(thingTypeUID, UID);
      }

      if (label != null) {
        thingBuilder.withLabel(label);
      }
      if (location != null) {
        thingBuilder.withLocation(location);
      }
      if (bridgeUID != null) {
        thingBuilder.withBridge(bridgeUID);
      }
      if (configuration != null) {
        thingBuilder.withConfiguration(configuration);
      }
      if (channels != null) {
        thingBuilder.withChannels(channels);
      }

      return thingBuilder.build();
    }
  }

  public static class Serializer extends StdSerializer<Thing> {

    public Serializer() {
      super(Thing.class);
    }

    @Override
    public void serialize(Thing value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeStartObject();
      gen.writeFieldName("kind");
      if (value instanceof Bridge) {
        gen.writeString("Bridge");

        gen.writeFieldName("thingTypeUID");
        gen.writeObject(value.getThingTypeUID());

        gen.writeFieldName("id");
        gen.writeObject(value.getUID().getId());
      } else {
        gen.writeString("Thing");

        gen.writeFieldName("UID");
        gen.writeObject(value.getUID());
      }

      if (value.getBridgeUID() != null) {
        gen.writeFieldName("bridgeUID");
        gen.writeObject(value.getBridgeUID());
      }

      if (value.getLabel() != null) {
        gen.writeFieldName("label");
        gen.writeString(value.getLabel());
      }

      if (value.getLocation() != null) {
        gen.writeFieldName("location");
        gen.writeString(value.getLocation());
      }

      if (value.getConfiguration() != null && value.getConfiguration().getProperties().size() > 0) {
        gen.writeFieldName("configuration");
        gen.writeObject(value.getConfiguration());
      }

      if (value.getChannels() != null && value.getChannels().size() > 0) {
        gen.writeFieldName("channels");
        gen.writeStartArray();
        for (Channel channel : value.getChannels()) {
          gen.writeObject(channel);
        }
        gen.writeEndArray();
      }
      gen.writeEndObject();
    }
  }

  static ThingUID constructId(ThingTypeUID thingType, ThingUID bridge, String id) {
    if (bridge == null) {
      return new ThingUID(thingType, id);
    }
    return new ThingUID(thingType, bridge, id);
  }

  static ThingUID constructId(ThingUID uid, ThingUID bridge) {
    if (bridge == null) {
      return uid;
    }
    return new ThingUID(new ExtraThingUID(uid.getAsString()).getThingType(), bridge, uid.getId());
  }

}
