/*
 * Copyright (C) 2023-2024 ConnectorIO Sp. z o.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.thing4.core.parser.thing.yaml.internal.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.BeanProperty.Bogus;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.InjectableValues.Std;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.builder.BridgeBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.thing4.core.parser.thing.yaml.internal.module.ThingModule.Channels;

public class ThingHandler {

  private static final ThingUID ROOT = new ThingUID("yaml:parent:root");

  static class Deserializer extends StdDeserializer<ThingNode> {

    private static final BeanProperty BOGUS = new Bogus();

    private final InjectableValues.Std injectableValues;

    public Deserializer(InjectableValues.Std injectableValues) {
      super(ThingNode.class);
      this.injectableValues = injectableValues;
    }

    @Override
    public ThingNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      ThingNode node = deserialize(p, ctxt, new ArrayDeque<>());
      return node;
    }

    private ThingNode deserialize(JsonParser p, DeserializationContext ctxt, Deque<ThingUID> stack) throws IOException, JsonProcessingException {
      if (p.getCurrentToken() != JsonToken.START_OBJECT) {
        throw new JsonMappingException(p, "Unexpected call of deserializer.");
      }

      String fieldName = null;
      boolean bridge = false;

      ThingTypeUID thingTypeUID = null;
      String id = null;
      String label = null;
      String location = null;
      ThingUID UID = stack.isEmpty() ? ROOT : null;
      ThingUID bridgeUID = stack.isEmpty() || stack.getLast() == ROOT ? null : stack.getLast();
      Configuration configuration = null;
      List<Channel> channels = null;
      List<ThingNode> things = null;

      while ((fieldName = p.nextFieldName()) != null) {
        if ("kind".equals(fieldName)) {
          bridge = "Bridge".equals(p.nextTextValue());
        } else if ("label".equals(fieldName)) {
          label = p.nextTextValue();
        } else if ("location".equals(fieldName)) {
          location = p.nextTextValue();
        } else if ("type".equals(fieldName)) {
          thingTypeUID = ctxt.readValue(p, ThingTypeUID.class);
          if (id != null) {
            if (UID != null) {
              throw new JsonMappingException(p, "The thing UID is defined and can't be overwritten by UID");
            }
            UID = constructId(thingTypeUID, bridgeUID, id);
          }
        } else if ("UID".equals(fieldName)) {
          if (UID != null) {
            throw new JsonMappingException(p, "Thing UID is already resolved and can't be overwritten!");
          }
          UID = ctxt.readValue(p, ThingUID.class);
        } else if ("bridge".equals(fieldName)) {
          if (bridgeUID != null) {
            throw new JsonMappingException(p, "Thing bridgeUID is already resolved and can't be overwritten!");
          }
          bridgeUID = ctxt.readValue(p, ThingUID.class);
        } else if ("id".equals(fieldName)) {
          id = p.nextTextValue();
          if (thingTypeUID != null) {
            if (UID != null) {
              throw new JsonMappingException(p, "The thing UID is defined and can't be overwritten by UID");
            }
            UID = constructId(thingTypeUID, bridgeUID, id);
          }
        } else if ("configuration".equals(fieldName)) {
          configuration = ctxt.readValue(p, Configuration.class);
        } else if ("channels".equals(fieldName)) {
          UID = push(injectableValues, UID);
          stack.addLast(UID);
          p.nextToken();
          channels = ctxt.readValue(p, Channels.class);
          pop(injectableValues);
          stack.removeLast();
        } else if ("things".equals(fieldName)) {
          UID = push(injectableValues, UID);
          stack.addLast(UID);
          p.nextToken(); // start array
          things = new ArrayList<>();
          while (p.nextToken() == JsonToken.START_OBJECT) {
            ThingNode deserialized = deserialize(p, ctxt, stack);
            things.add(deserialized);
          }
          pop(injectableValues);
          stack.removeLast();
        } else {
          throw new JsonMappingException(p, "Unrecognized field: " + fieldName);
        }
      }

      if (id != null && thingTypeUID == null) {
        throw new JsonMappingException(p, "Invalid mapping detected. When id is set the thingTypeUID must be provided.");
      }

      if (UID != ROOT) {
        if (thingTypeUID == null) {
          if (UID == null) {
            throw new JsonMappingException(p, "Unresolved ThingUID");
          }
          thingTypeUID = new ThingModule.ExtraThingUID(UID.getAsString()).getThingType();
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
        return new ThingNode(thingBuilder.build(), things);
      }
      return new ThingNode(null, things);
    }

    private void pop(Std injectableValues) throws JsonMappingException {
      injectableValues.addValue("thingUID", null);
    }

    private ThingUID push(Std injectableValues, ThingUID uid) throws JsonMappingException {
      if (uid != null) {
        injectableValues.addValue("thingUID", uid);
        return uid;
      } else {
        throw new JsonMappingException("ThingUID is not resolved yet, move definitions below UID or id/type fields.");
      }
    }
  }

  public static class Serializer extends StdSerializer<ThingNode> {

    public Serializer() {
      super(ThingNode.class);
    }


    @Override
    public void serialize(ThingNode node, JsonGenerator gen, SerializerProvider provider) throws IOException {
      serialize(node, gen, provider, null);
    }

    private void serialize(ThingNode node, JsonGenerator gen, SerializerProvider provider, ThingUID parent) throws IOException {
      gen.writeStartObject();

      Thing value = node.getThing();
      if (value != null) {
        if (value instanceof Bridge) {
          gen.writeFieldName("kind");
          gen.writeString("Bridge");
        }

        if (parent == null) {
          gen.writeFieldName("UID");
          gen.writeObject(value.getUID());
        } else {
          gen.writeFieldName("id");
          gen.writeObject(value.getUID().getId());
          gen.writeFieldName("type");
          gen.writeObject(value.getThingTypeUID());
        }

        if (value.getBridgeUID() != null) {
          if (parent != null && !parent.equals(value.getBridgeUID())) {
            throw new IllegalArgumentException("The bridgeUID of Thing does not match its parent Thing" + parent);
          }
          if (parent == null) {
            gen.writeFieldName("bridge");
            gen.writeObject(value.getBridgeUID());
          }
        }

        if (value.getLabel() != null && !value.getLabel().isBlank()) {
          gen.writeFieldName("label");
          gen.writeString(value.getLabel());
        }

        if (value.getLocation() != null && !value.getLocation().isBlank()) {
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
      }

      if (node.getThings() != null && node.getThings().size() > 0) {
        gen.writeFieldName("things");
        gen.writeStartArray();
        for (ThingNode thing : node.getThings()) {
          serialize(thing, gen, provider, value == null ? null : value.getUID());
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
    return new ThingUID(new ThingModule.ExtraThingUID(uid.getAsString()).getThingType(), bridge, uid.getId());
  }

}
