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
import org.openhab.core.thing.xml.internal.ThingDescriptionReader;

public abstract class ThingDescriptorCacheBase<T> extends DescriptorCache<List<T>> {

  private final Map<MavenProject, List<T>> cache = new ConcurrentHashMap<>();

  @Override
  public Optional<List<T>> get(MavenProject key) {
    return Optional.ofNullable(cache.computeIfAbsent(key, this::load));
  }

  private List<T> load(MavenProject project) {
    List<T> definitions = new ArrayList<>();
    ThingDescriptionReader reader = new ThingDescriptionReader();
    for (File file : getXmlFiles(project, "OH-INF/thing/")) {
      try {
        List<?> elements = reader.readFromXML(file.toURI().toURL());
        if (elements == null) {
          continue;
        }
        for (Object element : elements) {
          T definition = define(element);
          if (definition != null) {
            definitions.add(definition);
          }
        }
      } catch (MalformedURLException e) {
        // continue
      }
    }

    return definitions;
  }

  protected abstract T define(Object element);

}
