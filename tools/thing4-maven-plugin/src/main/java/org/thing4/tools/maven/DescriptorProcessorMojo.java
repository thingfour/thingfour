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

import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.openhab.core.config.core.ConfigDescription;
import org.openhab.core.thing.type.ChannelGroupType;
import org.openhab.core.thing.type.ChannelType;
import org.openhab.core.thing.type.ThingType;

/**
 * Mojo which works on top of common openHAB descriptors.
 */
public abstract class DescriptorProcessorMojo extends AbstractMojo {

  /**
   * The Maven Project.
   */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  @Inject
  private Cache<MavenProject, ConfigDescription, List<ConfigDescription>> configCache;

  @Inject
  private Cache<MavenProject, ThingType, List<ThingType>> thingCache;

  @Inject
  private Cache<MavenProject, ChannelType, List<ChannelType>> channelTypeCache;

  @Inject
  private Cache<MavenProject, ChannelGroupType, List<ChannelGroupType>> channelGroupTypeCache;

  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    configCache.get(project);
    thingCache.get(project);
    channelTypeCache.get(project);
    channelGroupTypeCache.get(project);

    process(
      configCache.get(project).orElse(Collections.emptyList()),
      thingCache.get(project).orElse(Collections.emptyList()),
      channelTypeCache.get(project).orElse(Collections.emptyList()),
      channelGroupTypeCache.get(project).orElse(Collections.emptyList())
    );
  }

  protected abstract void process(List<ConfigDescription> configDescriptions, List<ThingType> thingTypes,
    List<ChannelType> channelTypes, List<ChannelGroupType> channelGroupTypes)
    throws MojoExecutionException, MojoFailureException;

}
