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
package org.thing4.tools.maven;

import java.util.List;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.openhab.core.config.core.ConfigDescription;
import org.openhab.core.thing.type.ChannelGroupType;
import org.openhab.core.thing.type.ChannelType;
import org.openhab.core.thing.type.ThingType;

/**
 * Helper goal which allows to eagerly parse common descriptors.
 *
 * Since cache components are injected through Maven's IoC they are retained over execution of
 * other goals.
 */
@Mojo(name = "parse-descriptors",
  threadSafe = true,
  defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
  requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class LoadDescriptorsMojo extends DescriptorProcessorMojo {

  @Override
  protected void process(List<ConfigDescription> configDescriptions, List<ThingType> thingTypes,
      List<ChannelType> channelTypes, List<ChannelGroupType> channelGroupTypes) {
    // NO OP just preload descriptors
  }
}
