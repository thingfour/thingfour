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
package org.thing4.addons.model.thing;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openhab.core.thing.Thing;
import org.thing4.addons.model.thing.factory.DefaultBridgeBuilderFactory;
import org.thing4.addons.model.thing.factory.DefaultChannelBuilderFactory;
import org.thing4.addons.model.thing.factory.DefaultThingBuilderFactory;
import org.thing4.addons.model.thing.helper.OfficialParser;
import org.thing4.addons.model.thing.parser.AntlrV4ThingsParser;

public class ComparisonTest {

  private final OfficialParser officialParser = new OfficialParser();
  private final AntlrV4ThingsParser thing4parser = new AntlrV4ThingsParser(
    new DefaultBridgeBuilderFactory(),
    new DefaultThingBuilderFactory(),
    new DefaultChannelBuilderFactory()
  );

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("createFileList")
  public void testFile(String fileName, InputStream stream) throws IOException {
    byte[] contents = stream.readAllBytes();

    List<Thing> officialThings = officialParser.parse(contents);
    List<Thing> thing4Things = thing4parser.parse(contents);

    Assertions.assertThat(thing4Things).isEqualTo(officialThings);
  }

  public static Stream<Arguments> createFileList() {
    Class<ComparisonTest> lookupBase = ComparisonTest.class;
    return Stream.of(
      Arguments.of("test.things4", lookupBase.getResourceAsStream("/test.things4"))
      //Arguments.of("modbus.things4", lookupBase.getResourceAsStream("/modbus.things4"))
    );
  }

}
