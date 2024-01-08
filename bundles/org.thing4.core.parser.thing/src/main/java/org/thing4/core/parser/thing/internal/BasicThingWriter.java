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
package org.thing4.core.parser.thing.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.Thing;
import org.osgi.service.component.annotations.Component;
import org.thing4.core.parser.Writer;

@Component(service = Writer.class, property = {
    "type=" + AntlrV4ThingsParser.ID
})
public class BasicThingWriter implements Writer<Thing> {

  @Override
  public String getId() {
    return AntlrV4ThingsParser.ID;
  }

  @Override
  public Class<Thing> getType() {
    return Thing.class;
  }

  @Override
  public byte[] write(List<Thing> elements) {
    StringBuilder builder = new StringBuilder();

    for (Thing thing : elements) {
      builder.append(thing instanceof Bridge ? "Bridge " : "Thing ");
      builder.append(thing.getUID() + " ");
      if (thing.getLabel() != null) {
        builder.append("\"" + thing.getLabel() + "\" ");
      }
      if (thing.getBridgeUID() != null) {
        builder.append("(" + thing.getBridgeUID() + ") ");
      }
      if (thing.getLocation() != null) {
        builder.append("@ \"" + thing.getLocation() + "\" ");
      }

      if (thing.getConfiguration() != null) {
        // keep static sort order of parameters
        writeProperties("  ", thing.getConfiguration(), builder);
      }

      if (thing.getChannels() != null && thing.getChannels().size() > 0) {
        builder.append("  {\n");
        for (Channel channel : thing.getChannels()) {
          if (channel.getAcceptedItemType() != null) {
            builder.append("    " + channel.getKind().name() + " " + channel.getAcceptedItemType());
          } else {
            builder.append("    Type " + channel.getChannelTypeUID().getId());
          }
          builder.append(" : " + channel.getUID().getId() + " ");
          if (channel.getLabel() != null) {
            builder.append("\"" + channel.getLabel() + "\" ");
          }
          if (channel.getConfiguration() != null) {
            writeProperties("    ", channel.getConfiguration(), builder);
          }
        }
        builder.append("  }\n");
      }
      builder.append("\n");
    }

    return builder.toString().getBytes();
  }

  private void writeProperties(String prefix, Configuration configuration, StringBuilder builder) {
    if (configuration.getProperties() != null && configuration.getProperties().isEmpty()) {
      return;
    }

    Map<String, Object> properties = new TreeMap<>(configuration.getProperties());
    List<String> keys = new ArrayList<>(properties.keySet());
    builder.append("\n" + prefix + "[\n");
    for (int size = keys.size(), index = 0; index < size; index++) {
      String key = keys.get(index);
      builder.append(prefix + "  " + key + "=" + value(properties.get(key)));
      if (index + 1 < size) {
        builder.append(",");
      }
      builder.append("\n");
    }
    builder.append(prefix + "]\n");
  }

  private String value(Object value) {
    if (value instanceof String) {
      return "\"" + value + "\"";
    }

    return value.toString();
  }

}
