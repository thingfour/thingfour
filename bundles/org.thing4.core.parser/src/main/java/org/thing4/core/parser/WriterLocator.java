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
package org.thing4.core.parser;

import java.util.ServiceLoader;

/**
 * Locator which helps to find Writer instance.
 */
public class WriterLocator {

  public static <T> Writer<T> locate(String id, Class<T> type) {
    ServiceLoader<Writer> parsers = ServiceLoader.load(Writer.class);
    for (Writer<?> writer : parsers) {
      if (id.equals(writer.getId()) && type.equals(writer.getType())) {
        return (Writer<T>) writer;
      }
    }

    throw new IllegalArgumentException("Could not find writer " + id + " for type " + type);
  }

}
