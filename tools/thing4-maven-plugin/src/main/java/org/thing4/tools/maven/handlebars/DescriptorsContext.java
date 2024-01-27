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
package org.thing4.tools.maven.handlebars;

import java.util.List;
import org.openhab.core.config.core.ConfigDescription;
import org.openhab.core.thing.type.BridgeType;
import org.openhab.core.thing.type.ChannelGroupType;
import org.openhab.core.thing.type.ChannelType;
import org.openhab.core.thing.type.ThingType;

/**
 * Root object which is used by process descriptors goal.
 */
public class DescriptorsContext {

  private final List<BridgeType> bridgeTypes;
  private final List<ThingType> thingTypes;
  private final List<ChannelType> channelTypes;
  private final List<ChannelGroupType> channelGroupTypes;
  private final List<ConfigDescription> configDescriptions;

  public DescriptorsContext(List<BridgeType> bridgeTypes, List<ThingType> thingTypes, List<ChannelType> channelTypes,
      List<ChannelGroupType> channelGroupTypes, List<ConfigDescription> configDescriptions) {
    this.bridgeTypes = bridgeTypes;
    this.thingTypes = thingTypes;
    this.channelTypes = channelTypes;
    this.channelGroupTypes = channelGroupTypes;
    this.configDescriptions = configDescriptions;
  }

  public List<BridgeType> getBridgeTypes() {
    return bridgeTypes;
  }

  public List<ThingType> getThingTypes() {
    return thingTypes;
  }

  public List<ChannelType> getChannelTypes() {
    return channelTypes;
  }

  public List<ChannelGroupType> getChannelGroupTypes() {
    return channelGroupTypes;
  }

  public List<ConfigDescription> getConfigDescriptions() {
    return configDescriptions;
  }

}
