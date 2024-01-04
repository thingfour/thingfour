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
package org.thing4.core.parser.thing.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.openhab.core.thing.Thing;
import org.thing4.core.parser.thing.factory.DefaultBridgeBuilderFactory;
import org.thing4.core.parser.thing.factory.DefaultChannelBuilderFactory;
import org.thing4.core.parser.thing.factory.DefaultThingBuilderFactory;

class AntlrV4ThingsParserTest {

  @Test
  void verifyTestParser() throws Exception {
    AntlrV4ThingsParser parser = new AntlrV4ThingsParser(new DefaultBridgeBuilderFactory(), new DefaultThingBuilderFactory(), new DefaultChannelBuilderFactory());
    List<Thing> things = parser.parse(getClass().getResourceAsStream("/test.things4").readAllBytes());

    assertThat(things).isNotEmpty()
      .hasSize(2);
  }

}