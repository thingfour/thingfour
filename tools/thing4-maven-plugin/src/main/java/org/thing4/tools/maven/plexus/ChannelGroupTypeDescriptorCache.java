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
package org.thing4.tools.maven.plexus;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.openhab.core.thing.type.ChannelGroupType;
import org.openhab.core.thing.xml.internal.ChannelGroupTypeXmlResult;
import org.thing4.tools.maven.Cache;

@Component(role = Cache.class, hint = ChannelGroupTypeDescriptorCache.CHANNEL_GROUP_TYPE_HINT)
public class ChannelGroupTypeDescriptorCache extends ThingDescriptorCacheBase<ChannelGroupType> {

  public static final String CHANNEL_GROUP_TYPE_HINT = "channel-group-type";

  @Override
  protected ChannelGroupType define(MavenProject key, Object element) {
    if (element instanceof ChannelGroupTypeXmlResult) {
      return ((ChannelGroupTypeXmlResult) element).toChannelGroupType();
    }
    return null;
  }
}
