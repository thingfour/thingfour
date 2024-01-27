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

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.helper.DefaultHelperRegistry;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.URLTemplateSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.openhab.core.config.core.ConfigDescription;
import org.openhab.core.thing.type.BridgeType;
import org.openhab.core.thing.type.ChannelGroupType;
import org.openhab.core.thing.type.ChannelType;
import org.openhab.core.thing.type.ThingType;
import org.thing4.tools.maven.handlebars.ChannelTypeConfigDescriptorHelper;
import org.thing4.tools.maven.handlebars.DescriptorsContext;
import org.thing4.tools.maven.handlebars.FormatHelper;
import org.thing4.tools.maven.handlebars.ThingTypeConfigDescriptorHelper;

@Mojo(name = "process-descriptors",
    threadSafe = true,
    defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class DocumentationMojo extends DescriptorProcessorMojo {

  /**
   * The output directory into which to copy the resources.
   */
  @Parameter(defaultValue = "${project.build.directory}/docs", required = true)
  private File outputDirectory;

  /**
   * Extension for generated files.
   */
  @Parameter(required = true, readonly = true)
  private String outputExtension;

  /**
   * Reference prefix.
   */
  @Parameter(required = false, readonly = true, defaultValue = "xref:./")
  private String referencePrefix;

  /**
   * Reference suffix.
   */
  @Parameter(required = false, readonly = true, defaultValue = "")
  private String referenceSuffix;

  @Override
  protected void process(List<ConfigDescription> configDescriptions, List<ThingType> thingTypes,
    List<ChannelType> channelTypes, List<ChannelGroupType> channelGroupTypes)
    throws MojoExecutionException {

    Predicate<ThingType> filter = type -> type instanceof BridgeType;
    List<ThingType> thingTypeDescriptors = thingTypes.stream()
      .filter(filter.negate())
      .collect(Collectors.toList());
    List<BridgeType> bridgeTypeDescriptors = thingTypes.stream()
      .filter(filter)
      .map(BridgeType.class::cast)
      .collect(Collectors.toList());

    HelperRegistry registry = new DefaultHelperRegistry();
    registry.registerHelper("channelTypeConfigDescriptor", new ChannelTypeConfigDescriptorHelper(configDescriptions));
    registry.registerHelper("thingTypeConfigDescriptor", new ThingTypeConfigDescriptorHelper(configDescriptions));
    registry.registerHelper("format", new FormatHelper());
    Handlebars hbs = new Handlebars()
      .with(new ClassPathTemplateLoader("/templates/"))
      .with(EscapingStrategy.NOOP)
      .with(registry);

    Context context = Context.newBuilder(new DescriptorsContext(bridgeTypeDescriptors, thingTypeDescriptors, channelTypes, channelGroupTypes, configDescriptions))
      .resolver(
        new FieldValueResolver() {
          @Override
          public boolean matches(FieldWrapper field, String name) {
            // prevent reflection access issues
            if (field.getDeclaringClass().getPackage().getName().startsWith("java")) {
              return false;
            }
            return super.matches(field, name);
          }
        },
        MapValueResolver.INSTANCE,
        JavaBeanValueResolver.INSTANCE
      ).combine("year", LocalDate.now().getYear())
      .combine("referencePrefix", referencePrefix)
      .combine("referenceSuffix", referenceSuffix)
      .build();

    if (!bridgeTypeDescriptors.isEmpty()) {
      render("bridge-types.hbs", hbs, context);
    }
    if (!thingTypeDescriptors.isEmpty()) {
      render("thing-types.hbs", hbs, context);
    }
    if (!channelTypes.isEmpty()) {
      render("channel-types.hbs", hbs, context);
    }
    if (!configDescriptions.isEmpty()) {
      render("config-descriptions.hbs", hbs, context);
    }
  }

  private void render(String templateName, Handlebars hbs, Context context) throws MojoExecutionException {
    String templatePath = "/templates/" + templateName;
    URL template = getClass().getResource(templatePath);
    if (template == null) {
      throw new MojoExecutionException("Could not locate template file " + templatePath + " within classpath");
    }

    try {
      Template compiled = hbs.compile(new URLTemplateSource(templatePath, template));
      String outputName = templateName.substring(0, templateName.lastIndexOf('.'));
      String output = compiled.apply(context);
      if (!outputDirectory.exists()) {
        if (!outputDirectory.mkdirs()) {
          throw new MojoExecutionException("Could not create output directory " + outputDirectory);
        }
      }
      File outputFile = new File(outputDirectory, outputName + "." + outputExtension);

      Files.write(outputFile.toPath(), output.getBytes());
    } catch (IOException e) {
      throw new MojoExecutionException("Failed to write template " + templatePath + " output", e);
    }
  }

}
