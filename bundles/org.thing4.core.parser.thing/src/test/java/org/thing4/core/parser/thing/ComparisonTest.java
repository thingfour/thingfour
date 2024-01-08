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
package org.thing4.core.parser.thing;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.UID;
import org.thing4.addons.core.assertions.ThingAssertion;
import org.thing4.core.model.facade.ThingParserFacade;
import org.thing4.core.parser.thing.internal.factory.DefaultBridgeBuilderFactory;
import org.thing4.core.parser.thing.internal.factory.DefaultChannelBuilderFactory;
import org.thing4.core.parser.thing.internal.factory.DefaultThingBuilderFactory;
import org.thing4.core.parser.thing.internal.AntlrV4ThingsParser;
import org.thing4.core.parser.thing.internal.BasicThingWriter;

public class ComparisonTest {

  private final ThingParserFacade officialParser = new ThingParserFacade();
  private final AntlrV4ThingsParser thing4parser = new AntlrV4ThingsParser(
    new DefaultBridgeBuilderFactory(),
    new DefaultThingBuilderFactory(),
    new DefaultChannelBuilderFactory()
  );

  @ParameterizedTest
  @MethodSource("createFileList")
  public void testFile(String fileName) throws IOException {
    Class<ComparisonTest> lookupBase = ComparisonTest.class;
    InputStream stream = lookupBase.getResourceAsStream(fileName);
    if (stream == null) {
      Assertions.fail("Resource " + fileName + " not found");
    }
    byte[] contents = stream.readAllBytes();

    Map<UID, Thing> officialThings = officialParser.parse(contents).stream()
      .collect(Collectors.toMap(v -> v.getUID(), v -> v));
    Map<UID, Thing> thing4Things = thing4parser.parse(contents).stream()
      .collect(Collectors.toMap(v -> v.getUID(), v -> v));

    compare(officialThings, thing4Things);

    byte[] output = new BasicThingWriter().write(new ArrayList<>(officialThings.values()));
    System.out.println(new String(output));
    Map<UID, Thing> things = thing4parser.parse(output).stream()
      .collect(Collectors.toMap(v -> v.getUID(), v -> v));
    compare(officialThings, things);
  }

  private static void compare(Map<UID, Thing> reference, Map<UID, Thing> target) {
    for (Entry<UID, Thing> entry : reference.entrySet()) {
      assertThat(target).containsKey(entry.getKey());

      Thing a = entry.getValue();
      Thing thing4 = target.get(entry.getKey());

      new ThingAssertion(thing4).isEqualTo(a);
    }
  }

  public static Stream<Arguments> createFileList() {
    return Stream.of(
      Arguments.of("/test.things4"),
      Arguments.of("/modbus.things4")
    );
  }

}
