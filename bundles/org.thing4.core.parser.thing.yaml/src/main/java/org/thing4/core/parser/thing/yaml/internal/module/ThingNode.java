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

import java.util.Comparator;
import java.util.List;
import org.openhab.core.thing.Thing;

public class ThingNode {

  private Thing thing;

  private List<ThingNode> things;

  public ThingNode(Thing thing, List<ThingNode> things) {
    this.thing = thing;
    this.things = things;
  }

  public void append(ThingNode thing) {
    things.add(thing);
  }

  public void sort(Comparator<Thing> comparator) {
    //things.sort(comparator);
  }

  public Thing getThing() {
    return thing;
  }

  public List<ThingNode> getThings() {
    return things;
  }

  @Override
  public String toString() {
    return "ThingNode [" + (thing == null ? "<root>" : thing.getUID()) + ", " + things + "]";
  }
}
