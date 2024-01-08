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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.function.Function;
import org.openhab.core.thing.UID;

public class UIDHandler {

  public static <T extends UID> JsonDeserializer<T> deserializer(Class<T> type, Function<String, T> creator) {
    return new UIDDeserializer<>(creator);
  }

  public static <T extends UID> JsonSerializer<T> serializer(Class<T> type) {
    return new UIDSerializer<>(type);
  }

  static class UIDDeserializer<T extends UID> extends StdDeserializer<T> {

    private final Function<String, T> creator;

    public UIDDeserializer(Function<String, T> creator) {
      super(UID.class);
      this.creator = creator;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return creator.apply(p.nextTextValue());
    }
  }

  static class UIDSerializer<T extends UID> extends StdSerializer<T> {

    public UIDSerializer(Class<T> type) {
      super(type);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeString(value.getAsString());
    }

  }

}
