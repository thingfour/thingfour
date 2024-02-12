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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.thing4.tools.maven.Cache;

public abstract class DescriptorCache<E, V extends Collection<E>> implements Cache<MavenProject, E, V> {

  protected final File[] getXmlFiles(MavenProject project, String subdirectory) {
    List<File> files = new ArrayList<>();
    for (Resource resource : project.getResources()) {
      File directory = new File(resource.getDirectory(), subdirectory);
      if (directory.isDirectory()) {
        File[] xmlFiles = directory.listFiles(file -> file.getName().endsWith(".xml"));
        if (xmlFiles == null) {
          continue;
        }
        files.addAll(Arrays.asList(xmlFiles));
      }
    }

    return files.toArray(new File[0]);
  }

}
