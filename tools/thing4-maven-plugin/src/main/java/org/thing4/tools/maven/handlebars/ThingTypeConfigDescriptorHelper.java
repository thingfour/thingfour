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

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.openhab.core.config.core.ConfigDescription;
import org.openhab.core.thing.type.ThingType;

public class ThingTypeConfigDescriptorHelper implements Helper<ThingType> {

  private final List<ConfigDescription> descriptors;

  public ThingTypeConfigDescriptorHelper(List<ConfigDescription> descriptors) {
    this.descriptors = descriptors;
  }

  @Override
  public Object apply(ThingType context, Options options) throws IOException {
    URI uri = context.getConfigDescriptionURI();

    for (ConfigDescription description : descriptors) {
      if (uri != null && uri.toString().equals(description.getUID().toString())) {
        return description;
      }
    }
    return null;
  }
}

