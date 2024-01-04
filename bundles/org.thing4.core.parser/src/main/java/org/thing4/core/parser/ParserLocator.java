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
 * Locator which helps to find Parser instance.
 */
public class ParserLocator {

  public static <T> Parser<T> locate(String id, Class<T> type) {
    ServiceLoader<Parser> parsers = ServiceLoader.load(Parser.class);
    for (Parser<?> parser : parsers) {
      if (id.equals(parser.getId()) && type.equals(parser.getType())) {
        return (Parser<T>) parser;
      }
    }

    throw new IllegalArgumentException("Could not find parser " + id + " for type " + type);
  }

}
