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
package org.thing4.addons.model.facade.osgi;

import org.openhab.core.config.core.ConfigDescriptionRegistry;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.openhab.core.thing.type.ThingType;
import org.openhab.core.thing.type.ThingTypeRegistry;

public class DummyThingHandlerFactory implements ThingHandlerFactory {

  private final ThingTypeRegistry thingTypeRegistry;
  private final ConfigDescriptionRegistry configDescriptionRegistry;

  public DummyThingHandlerFactory(ThingTypeRegistry thingTypeRegistry, ConfigDescriptionRegistry configDescriptionRegistry) {
    this.thingTypeRegistry = thingTypeRegistry;
    this.configDescriptionRegistry = configDescriptionRegistry;
  }

  @Override
  public boolean supportsThingType(ThingTypeUID thingTypeUID) {
    return true;
  }

  @Override
  public ThingHandler registerHandler(Thing thing) {
    return new DummyThingHandler(thing);
  }

  @Override
  public void unregisterHandler(Thing thing) {

  }

  @Override
  public Thing createThing(ThingTypeUID thingTypeUID, Configuration configuration, ThingUID thingUID, ThingUID bridgeUID) {
    ThingUID effectiveUID = thingUID != null ? thingUID : ThingFactory.generateRandomThingUID(thingTypeUID);
    ThingType thingType = thingTypeRegistry.getThingType(thingTypeUID, null);
    if (thingType != null) {
      return ThingFactory.createThing(thingType, effectiveUID, configuration, bridgeUID, configDescriptionRegistry);
    } else {
      return null;
    }
  }

  @Override
  public void removeThing(ThingUID thingUID) {

  }

}
