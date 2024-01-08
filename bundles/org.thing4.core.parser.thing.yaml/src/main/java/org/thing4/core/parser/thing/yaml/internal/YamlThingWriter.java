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
package org.thing4.core.parser.thing.yaml.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.util.List;
import org.openhab.core.thing.Thing;
import org.osgi.service.component.annotations.Component;
import org.thing4.core.parser.Writer;
import org.thing4.core.parser.thing.yaml.internal.module.ThingModule;
import org.thing4.core.parser.thing.yaml.internal.module.ThingNode;

/**
 * Thing writer based on yaml syntax/format.
 */
@Component(service = Writer.class, property = {
    "type=" + YamlThingParser.ID
})
public class YamlThingWriter implements Writer<Thing> {

  private final ObjectMapper mapper;

  public YamlThingWriter() {
    mapper = new ObjectMapper(new YAMLFactory());
    mapper.registerModule(new ThingModule(new InjectableValues.Std()));
  }

  @Override
  public String getId() {
    return YamlThingParser.ID;
  }

  @Override
  public Class<Thing> getType() {
    return Thing.class;
  }

  @Override
  public byte[] write(List<Thing> elements) {;
    try {
      Hierarchy hierarchy = new Hierarchy(elements);
      // root node
      ThingNode node = hierarchy.getChildrenMap().get("");
      if (node != null) {
        return mapper.writer().writeValueAsBytes(node);
      }
      return new byte[0];
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
