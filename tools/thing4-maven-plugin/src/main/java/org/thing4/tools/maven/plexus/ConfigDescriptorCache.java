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

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.openhab.core.config.core.ConfigDescription;
import org.openhab.core.config.core.xml.internal.ConfigDescriptionReader;
import org.thing4.tools.maven.Cache;

@Component(role = Cache.class, hint = ConfigDescriptorCache.HINT)
public class ConfigDescriptorCache extends DescriptorCache<List<ConfigDescription>> {

  public static final String HINT = "config-description";

  private final Map<MavenProject, List<ConfigDescription>> cache = new ConcurrentHashMap<>();

  @Override
  public Optional<List<ConfigDescription>> get(MavenProject key) {
    return Optional.ofNullable(cache.computeIfAbsent(key, this::load));
  }

  private List<ConfigDescription> load(MavenProject project) {
    List<ConfigDescription> configDescriptions = new ArrayList<>();
    ConfigDescriptionReader reader = new ConfigDescriptionReader();
    for (File file : getXmlFiles(project, "OH-INF/config/")) {
      try {
        List<ConfigDescription> elements = reader.readFromXML(file.toURI().toURL());
        if (elements == null) {
          continue;
        }
        configDescriptions.addAll(elements);
      } catch (MalformedURLException e) {
        // continue
      }
    }

    return configDescriptions;
  }

}
