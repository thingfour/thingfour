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

import java.util.Objects;
import org.assertj.core.api.ObjectAssert;
import org.openhab.core.thing.Channel;

public class ChannelAssertion extends ObjectAssert<Channel> {

  public ChannelAssertion(Channel channel) {
    super(channel);
  }

  public ChannelAssertion isEqualTo(Channel other) {
    if (!Objects.deepEquals(actual.getAcceptedItemType(), other.getAcceptedItemType())) {
      failWithMessage("The AcceptedItemType do not match");
    }
    if (!Objects.deepEquals(actual.getKind(), other.getKind())) {
      failWithMessage("The Kind do not match");
    }
    if (!Objects.deepEquals(actual.getUID(), other.getUID())) {
      failWithMessage("The UID do not match");
    }
    if (!Objects.deepEquals(actual.getChannelTypeUID(), other.getChannelTypeUID())) {
      failWithMessage("The ChannelTypeUID do not match");
    }
    if (!Objects.deepEquals(actual.getLabel(), other.getLabel())) {
      failWithMessage("The Label do not match");
    }
    if (!Objects.deepEquals(actual.getDescription(), other.getDescription())) {
      failWithMessage("The Description do not match");
    }
    if (!Objects.deepEquals(actual.getConfiguration(), other.getConfiguration())) {
      failWithMessage("The Configuration do not match");
    }
    if (!Objects.deepEquals(actual.getProperties(), other.getProperties())) {
      failWithMessage("The Properties do not match");
    }
    if (!Objects.deepEquals(actual.getDefaultTags(), other.getDefaultTags())) {
      failWithMessage("The DefaultTags do not match");
    }
    if (!Objects.deepEquals(actual.getAutoUpdatePolicy(), other.getAutoUpdatePolicy())) {
      failWithMessage("The AutoUpdatePolicy do not match");

    }
    return this;
  }

}
