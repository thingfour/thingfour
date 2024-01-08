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
package org.thing4.core.provider.file.internal;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.openhab.core.OpenHAB;
import org.openhab.core.service.AbstractWatchService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.thing4.core.provider.ManagedProvider;
import org.thing4.core.provider.file.FileProcessor;

/**
 * Directory watcher which listens to directory entries and dispatch information about files
 * to a provider to parse them.
 */
@Component
public class Thing4FileWatcher extends AbstractWatchService {

  // The program argument name for setting the service config directory path
  public static final String THING4_DIR_ARGUMENT = "openhab.thing4";

  // Default subdirectory
  public static final String THING4_THINGS_FOLDER = "thing4";

  private final Map<String, Entry<ServiceRegistration<?>, ManagedProvider<?, ?>>> providers = new ConcurrentHashMap<>();

  private final Map<String, FileProcessor> processors = new ConcurrentHashMap<>();

  private final Map<String, Set<Entry<Kind, Path>>> unprocessed = new ConcurrentHashMap<>();
  private final BundleContext context;

  @Activate
  public Thing4FileWatcher(BundleContext context) {
    super(getPathToWatch());
    this.context = context;
  }

  private static String getPathToWatch() {
    String customValue = System.getProperty(THING4_DIR_ARGUMENT);
    if (customValue != null) {
      return OpenHAB.getConfigFolder() + File.separator + customValue;
    }

    return OpenHAB.getConfigFolder() + File.separator + THING4_THINGS_FOLDER;
  }

  @Override
  protected boolean watchSubDirectories() {
    return true;
  }

  @Override
  protected Kind<?>[] getWatchEventKinds(Path subDir) {
    return new Kind<?>[] { ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY };
  }

  @Reference
  public void addProcessor(FileProcessor processor) {
    String type = processor.getId();
    processors.put(type, processor);

    if (unprocessed.containsKey(type)) {
      Set<Entry<Kind, Path>> entries = unprocessed.remove(type);
      for (Entry<Kind, Path> entry : entries) {
        process(processor, entry.getValue());
      }
    }
  }

  public void removeParser(FileProcessor processor) {
    processors.remove(processor.getId());
  }

  @Override
  protected void processWatchEvent(WatchEvent<?> event, Kind<?> kind, Path path) {
    File file = path.toFile();

    String remaining = file.getPath().substring(pathToWatch.length());
    if (!remaining.contains("/")) {
      return;
    }

    String type = remaining.substring(0, remaining.indexOf('/'));
    if (!processors.containsKey(type)) {
      if (!unprocessed.containsKey(type)) {
        unprocessed.put(type, new LinkedHashSet<>());
      }
      unprocessed.get(type).add(new SimpleEntry<>(kind, path));
      return;
    }

    if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
      if (providers.containsKey(file.getPath())) {
        remove(file);
      }
      process(processors.get(type), path);
    } else if (kind == ENTRY_DELETE) {
      if (providers.containsKey(file.getPath())) {
        remove(file);
      }
    }
  }

  private void remove(File file) {
    Entry<ServiceRegistration<?>, ManagedProvider<?, ?>> entry = providers.remove(file.getPath());
    entry.getValue().removeAll();
    entry.getKey().unregister();
  }

  private void process(FileProcessor<?, ?> processor, Path path) {
    File file = path.toFile();
    try (FileInputStream fis = new FileInputStream(file)) {
      byte[] contents = fis.readAllBytes();

      ManagedProvider<?, ?> provider = processor.process(file, contents);
      String[] interfaces = getInterfaces(provider.getClass());
      ServiceRegistration<?> registration = context.registerService(interfaces, provider, new Hashtable<>());
      providers.put(file.getPath(), new SimpleEntry<>(registration, provider));
    } catch (IOException e) {
      logger.error("Could not register things declared in file {}.", file, e);
    }
  }

  private static String[] getInterfaces(Class<?> type) {
    List<String> interfaces = new ArrayList<>();
    do {
      for (Class<?> iface : type.getInterfaces()) {
        interfaces.add(iface.getName());
      }
      type = type.getSuperclass();
    } while (type.getSuperclass() != Object.class);
    return interfaces.toArray(String[]::new);
  }

}
