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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingUID;
import org.thing4.core.parser.thing.yaml.internal.module.ThingNode;

public class Hierarchy {

  private Map<String, ThingNode> childrenMap = new TreeMap<>();

  public Hierarchy(List<Thing> things) {
    for (Thing thing : things) {
      ThingUID uid = thing.getUID();
      List<String> bridgeIds = uid.getBridgeIds();

      ThingNode node = null;
      if (!childrenMap.containsKey(uid.getId())) {
        // register parent
        node = new ThingNode(thing, new ArrayList<>());
        childrenMap.put(uid.getId(), node);
      } else {
        // populate parent information with already recognized children
        ThingNode children = childrenMap.get(uid.getId());
        node = new ThingNode(thing, children.getThings());
        childrenMap.put(uid.getId(), node);
      }

      if (bridgeIds.isEmpty()) {
        append(childrenMap, "", node);
        continue;
      }

      String parent = bridgeIds.get(bridgeIds.size() - 1);
      append(childrenMap, parent, node);
    }
  }

  public Map<String, ThingNode> getChildrenMap() {
    return childrenMap;
  }

  private static void append(Map<String, ThingNode> map, String key, ThingNode node) {
    if (!map.containsKey(key)) {
      map.put(key, new ThingNode(null, new ArrayList<>()));
    }
    map.get(key).append(node);
  }


}
