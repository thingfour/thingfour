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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.InjectableValues.Std;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.type.ChannelKind;
import org.openhab.core.thing.type.ChannelTypeUID;

public class ChannelHandler {

  static class ChannelDeserializer extends StdDeserializer<Channel> {

    private final InjectableValues.Std injectableValues;

    public ChannelDeserializer(InjectableValues.Std injectableValues) {
      super(Channel.class);
      this.injectableValues = injectableValues;
    }

    public Channel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.getCurrentToken() != JsonToken.START_OBJECT) {
        throw new JsonMappingException(p, "Unexpected call of deserializer.");
      }

      String id = null;
      String type = null;
      ChannelKind kind = ChannelKind.STATE;
      String label = null;
      String description = null;
      Configuration configuration = null;

      String fieldName = null;
      while ((fieldName = p.nextFieldName()) != null) {
        if ("id".equals(fieldName)) {
          id = p.nextTextValue();
        } else if ("kind".equals(fieldName)) {
          kind = ChannelKind.parse(p.nextTextValue());
        } else if ("label".equals(fieldName)) {
          label = p.nextTextValue();
        } else if ("description".equals(fieldName)) {
          description = p.nextTextValue();
        } else if ("type".equals(fieldName)) {
          type = p.nextTextValue();
        } else if ("configuration".equals(fieldName)) {
          configuration = ctxt.readValue(p, Configuration.class);
        } else {
          throw new JsonMappingException(p, "Unrecognized field: " + fieldName);
        }
      }

      if (id == null || type == null) {
        throw new JsonMappingException(p, "Invalid mapping detected - neither id or type of channel is set");
      }

      Object thingUID = injectableValues.findInjectableValue("thingUID", ctxt, null, null);
      if (!(thingUID instanceof ThingUID)) {
        throw new JsonMappingException(p, "Invalid mapping detected - unknown thing context");
      }

      ThingUID thing = (ThingUID) thingUID;
      ChannelBuilder channelBuilder = ChannelBuilder.create(new ChannelUID(thing, id))
        .withKind(kind);
      if (type != null) {
        channelBuilder.withType(new ChannelTypeUID(thing.getBindingId(), type));
      }

      if (label != null) {
        channelBuilder.withLabel(label);
      }
      if (description != null) {
        channelBuilder.withDescription(description);
      }
      if (configuration != null) {
        channelBuilder.withConfiguration(configuration);
      }

      return channelBuilder.build();
    }
  }

  static class ChannelSerializer extends StdSerializer<Channel> {

    public ChannelSerializer() {
      super(Channel.class);
    }

    @Override
    public void serialize(Channel value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeStartObject();

      if (value.getKind() != ChannelKind.STATE) {
        gen.writeFieldName("kind");
        gen.writeString(value.getKind().name());
      }

      gen.writeFieldName("id");
      gen.writeString(value.getUID().getId());
      gen.writeFieldName("type");
      gen.writeString(value.getChannelTypeUID().getId());

      if (value.getLabel() != null && !value.getLabel().isBlank()) {
        gen.writeFieldName("label");
        gen.writeString(value.getLabel());
      }
      if (value.getDescription() != null && !value.getDescription().isBlank()) {
        gen.writeFieldName("description");
        gen.writeString(value.getDescription());
      }
      if (value.getConfiguration() != null) {
        gen.writeFieldName("configuration");
        gen.writeObject(value.getConfiguration().getProperties());
      }

      gen.writeEndObject();
    }
  }
}
