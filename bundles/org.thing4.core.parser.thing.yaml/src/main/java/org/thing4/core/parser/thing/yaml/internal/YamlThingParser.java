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

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openhab.core.thing.Thing;
import org.osgi.service.component.annotations.Component;
import org.thing4.core.parser.Parser;
import org.thing4.core.parser.thing.yaml.internal.module.ThingModule;
import org.thing4.core.parser.thing.yaml.internal.module.ThingNode;

/**
 * Thing parser based on yaml syntax/format.
 */
@Component(service = Parser.class, property = {
    "type=" + YamlThingParser.ID
})
public class YamlThingParser implements Parser<Thing> {

  public static final String ID = "yaml";
  private final ObjectMapper mapper;

  public YamlThingParser() {
    mapper = new ObjectMapper(new YAMLFactory());
    mapper.registerModule(new ThingModule(new InjectableValues.Std()));
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public Class<Thing> getType() {
    return Thing.class;
  }

  @Override
  public List<Thing> parse(byte[] contents) {
    if (contents.length == 0) {
      return new ArrayList<>();
    }

    try {
      ThingNode things = mapper.readerFor(ThingNode.class).readValue(contents);
      if (things != null) {
        // root node does not have thing definition, only "things"
        return flatten(things.getThings());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new ArrayList<>();
  }

  private List<Thing> flatten(List<ThingNode> things) {
    return flatten(new ArrayList<>(), things);
  }

  private List<Thing> flatten(ArrayList<Thing> collection, List<ThingNode> things) {
    for (ThingNode node : things) {
      collection.add(node.getThing());
      if (node.getThings() != null) {
        flatten(collection, node.getThings());
      }
    }
    return collection;
  }

  @Override
  public List<Thing> parse(String contents) {
    return parse(contents.getBytes());
  }

}
