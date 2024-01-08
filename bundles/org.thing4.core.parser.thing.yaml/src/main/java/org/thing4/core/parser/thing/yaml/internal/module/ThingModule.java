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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.util.Converter.None;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.thing4.core.parser.thing.yaml.internal.module.ChannelHandler.ChannelDeserializer;
import org.thing4.core.parser.thing.yaml.internal.module.ChannelHandler.ChannelSerializer;

public class ThingModule extends SimpleModule {

  @JsonDeserialize(converter = None.class)
  private Object any;

  public ThingModule(InjectableValues.Std injectableValues) {
    addDeserializer(ThingNode.class, new ThingHandler.Deserializer(injectableValues));
    addSerializer(new ThingHandler.Serializer());
    addDeserializer(Channel.class, new ChannelDeserializer(injectableValues));
    addSerializer(new ChannelSerializer());


    addDeserializer(Configuration.class, new StdDeserializer<>(Configuration.class) {
      @Override
      public Configuration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        p.nextValue();
        Map<String, Object> config = p.readValueAs(new TypeReference<Map<String, Object>>() {});
        return new Configuration(config);
      }
    });
    addSerializer(Configuration.class, new StdSerializer<Configuration>(Configuration.class) {
      @Override
      public void serialize(Configuration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObject(value.getProperties());
      }
    });

    // UIDs
    addDeserializer(ThingUID.class, UIDHandler.deserializer(ThingUID.class, ThingUID::new));
    addSerializer(ThingUID.class, UIDHandler.serializer(ThingUID.class));
    addDeserializer(ThingTypeUID.class, UIDHandler.deserializer(ThingTypeUID.class, ThingTypeUID::new));
    addSerializer(ThingTypeUID.class, UIDHandler.serializer(ThingTypeUID.class));
    addDeserializer(ChannelTypeUID.class, UIDHandler.deserializer(ChannelTypeUID.class, ChannelTypeUID::new));
    addSerializer(ChannelTypeUID.class, UIDHandler.serializer(ChannelTypeUID.class));
    addDeserializer(ChannelUID.class, UIDHandler.deserializer(ChannelUID.class, ChannelUID::new));
    addSerializer(ChannelUID.class, UIDHandler.serializer(ChannelUID.class));
  }

  static class ExtraThingUID extends ThingUID {

    public ExtraThingUID(String id) {
      super(id);
    }

    public ThingTypeUID getThingType() {
      return new ThingTypeUID(getSegment(0), getSegment(1));
    }
  }

  static class Channels extends ArrayList<Channel> { }
  static class ThingNodes extends ArrayList<ThingNode> { }

}
