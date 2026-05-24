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
package org.thing4.addons.core.assertions;

import java.util.List;
import java.util.Objects;
import org.assertj.core.api.ObjectAssert;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.Thing;

public class ThingAssertion extends ObjectAssert<Thing> {

  public ThingAssertion(Thing thing) {
    super(thing);
  }

  public ThingAssertion isEqualTo(Thing other) {
    if (!Objects.deepEquals(actual.getLabel(), other.getLabel())) {
      failWithMessage("The Label do not match");
    }
    if (!Objects.deepEquals(actual.getStatus(), other.getStatus())) {
      failWithMessage("The Status do not match");
    }
    if (!Objects.deepEquals(actual.getStatusInfo(), other.getStatusInfo())) {
      failWithMessage("The StatusInfo do not match");
    }
    if (!Objects.deepEquals(actual.getHandler(), other.getHandler())) {
      failWithMessage("The Handler do not match");
    }
    if (!Objects.deepEquals(actual.getBridgeUID(), other.getBridgeUID())) {
      failWithMessage("The BridgeUID do not match");
    }
    if (!Objects.deepEquals(actual.getConfiguration(), other.getConfiguration())) {
      failWithMessage("The Configuration do not match");
    }
    if (!Objects.deepEquals(actual.getUID(), other.getUID())) {
      failWithMessage("The UID do not match");
    }
    if (!Objects.deepEquals(actual.getThingTypeUID(), other.getThingTypeUID())) {
      failWithMessage("The ThingTypeUID do not match");
    }
    if (!Objects.deepEquals(actual.getProperties(), other.getProperties())) {
      failWithMessage("The Properties do not match");
    }
    if (!Objects.deepEquals(actual.getLocation(), other.getLocation())) {
      failWithMessage("The Location do not match");
    }
    if (!Objects.deepEquals(actual.isEnabled(), other.isEnabled())) {
      failWithMessage("The nabled do not match");
    }

    List<Channel> channels = actual.getChannels();
    if (channels == null && other.getChannels() != null) {
      failWithMessage("The Channels do not match");
    }
    if (channels.size() != other.getChannels().size()) {
      failWithMessage("The Channels do not match");
    }

    for (int size = channels.size(), idx = 0; idx < size; idx++) {
      Channel channel = channels.get(idx);
      Channel otherChannel = other.getChannels().get(idx);

      new ChannelAssertion(channel).isEqualTo(otherChannel);
    }

    return this;
  }
}
