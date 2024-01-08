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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.thing4.core.parser.Parser;
import org.thing4.core.parser.ParserLocator;
import org.thing4.core.parser.Writer;
import org.thing4.core.parser.WriterLocator;

/**
 * Mojo to parse DSL resources.
 */
@Mojo(name = "parse-write",
  threadSafe = true,
  defaultPhase = LifecyclePhase.VALIDATE,
  requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class ParseWriteMojo extends AbstractMojo {

  /**
   * The Maven Project.
   */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  /**
   * List of files to include in parsing.
   */
  @Parameter
  private List<String> includes;

  /**
   * List of files to exclude in parsing.
   */
  @Parameter
  private List<String> excludes;

  /**
   * Parser identifier.
   */
  @Parameter(required = true, readonly = true)
  private String parser;

  /**
   * Writer identifier.
   */
  @Parameter(required = false, readonly = true)
  private String writer;

  /**
   * Type of parsed entity (Thing, Item, Sitemap).
   */
  @Parameter(required = true, readonly = true)
  private String type;

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void execute() throws MojoExecutionException, MojoFailureException {
    Class<?> clazz = null;
    try {
       clazz = Class.forName(type);
    } catch (ClassNotFoundException e) {
      throw new MojoExecutionException(e);
    }

    Parser<?> fileParser = ParserLocator.locate(parser, clazz);
    Writer<?> fileWriter = null;
    if (writer != null) {
      fileWriter = WriterLocator.locate(writer, clazz);
    }

    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(project.getBasedir());

    if (includes != null && !includes.isEmpty()) {
      scanner.setIncludes(includes.toArray(String[]::new));
    }
    if (excludes != null && !excludes.isEmpty()) {
      scanner.setIncludes(excludes.toArray(String[]::new));
    }

    scanner.scan();
    String[] files = scanner.getIncludedFiles();

    for (String file : files) {
      try (FileInputStream fis = new FileInputStream(new File(project.getBasedir(), file))) {
        List objects = fileParser.parse(fis.readAllBytes());
        if (fileWriter != null) {
          String fileName = file.substring(0, file.lastIndexOf("."));
          try (FileOutputStream fos = new FileOutputStream(new File(project.getBasedir(), fileName + "." + writer))) {
            byte[] output = fileWriter.write(objects);
            if (output.length != 0) {
              fos.write(output);
            }
          } catch (IOException e) {
            throw new MojoExecutionException("Could not store generated content", e);
          }
        }
      } catch (IOException e) {
        throw new MojoFailureException("Could not parse file " + file, e);
      }
    }
  }

}
