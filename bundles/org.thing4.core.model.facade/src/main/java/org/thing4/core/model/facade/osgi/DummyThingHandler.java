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
package org.thing4.core.model.facade.osgi;

import java.util.Map;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerCallback;
import org.openhab.core.types.Command;

public class DummyThingHandler implements ThingHandler {

  private final Thing thing;

  public DummyThingHandler(Thing thing) {
    this.thing = thing;
  }

  @Override
  public Thing getThing() {
    return thing;
  }

  @Override
  public void initialize() {

  }

  @Override
  public void dispose() {

  }

  @Override
  public void setCallback(ThingHandlerCallback thingHandlerCallback) {

  }

  @Override
  public void handleCommand(ChannelUID channelUID, Command command) {

  }

  @Override
  public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {

  }

  @Override
  public void thingUpdated(Thing thing) {

  }

  @Override
  public void channelLinked(ChannelUID channelUID) {

  }

  @Override
  public void channelUnlinked(ChannelUID channelUID) {

  }

  @Override
  public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {

  }

  @Override
  public void handleRemoval() {

  }
}
