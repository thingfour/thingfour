package org.thing4.core.parser.thing.yaml;

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

class YamlThingParserTest {

  private final ThingParserFacade officialParser = new ThingParserFacade();
  private final YamlThingParser yamlParser = new YamlThingParser(
//      new DefaultBridgeBuilderFactory(),
//      new DefaultThingBuilderFactory(),
//      new DefaultChannelBuilderFactory()
  );

  @ParameterizedTest
  @MethodSource("createFileList")
  public void testFile(String thingFile, String yamlFile) throws IOException {
    Class<YamlThingParserTest> lookupBase = YamlThingParserTest.class;
    InputStream stream = lookupBase.getResourceAsStream(yamlFile);
    if (stream == null) {
      Assertions.fail("Resource " + yamlFile + " not found");
    }
    byte[] yamlContents = stream.readAllBytes();

    stream = lookupBase.getResourceAsStream(thingFile);
    if (stream == null) {
      Assertions.fail("Resource " + thingFile + " not found");
    }
    byte[] thingContents = stream.readAllBytes();

    Map<UID, Thing> officialThings = officialParser.parse(thingContents).stream()
        .collect(Collectors.toMap(v -> v.getUID(), v -> v));
    Map<UID, Thing> thing4Things = yamlParser.parse(yamlContents).stream()
        .collect(Collectors.toMap(v -> v.getUID(), v -> v));

    compare(officialThings, thing4Things);

    byte[] output = new YamlThingWriter().write(new ArrayList<>(officialThings.values()));
    System.out.println(new String(output));
    Map<UID, Thing> things = yamlParser.parse(output).stream()
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
        Arguments.of("/test.things4", "/test.yaml"),
        Arguments.of("/modbus.things4", "/modbus.yaml")
    );
  }



}