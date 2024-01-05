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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.builder.BridgeBuilder;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.openhab.core.thing.type.AutoUpdatePolicy;
import org.openhab.core.thing.type.ChannelKind;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.thing4.core.parser.thing.ThingsParser.ModelBridgeContext;
import org.thing4.core.parser.thing.ThingsParser.ModelChannelContext;
import org.thing4.core.parser.thing.ThingsParser.ModelPropertiesContext;
import org.thing4.core.parser.thing.ThingsParser.ModelPropertyContext;
import org.thing4.core.parser.thing.ThingsParser.ModelThingContext;
import org.thing4.core.parser.thing.ThingsParser.NestedBridgeContext;
import org.thing4.core.parser.thing.ThingsParser.NestedThingContext;
import org.thing4.core.parser.thing.ThingsParser.UidContext;
import org.thing4.core.parser.thing.ThingsParser.UidSegmentContext;
import org.thing4.core.parser.thing.ThingsParser.ValueTypeContext;
import org.thing4.core.parser.thing.factory.DefaultBridgeBuilderFactory;
import org.thing4.core.parser.thing.factory.DefaultChannelBuilderFactory;
import org.thing4.core.parser.thing.factory.DefaultThingBuilderFactory;
import org.thing4.core.parser.Parser;
import org.thing4.core.parser.thing.antlr.ThingsErrorListener;

public class AntlrV4ThingsParser implements Parser<Thing> {

  private final BridgeBuilderFactory bridgeFactory;
  private final ThingBuilderFactory thingFactory;

  private final ChannelBuilderFactory channelFactory;

  public AntlrV4ThingsParser() {
    this(new DefaultBridgeBuilderFactory(), new DefaultThingBuilderFactory(), new DefaultChannelBuilderFactory());
  }

  public AntlrV4ThingsParser(BridgeBuilderFactory bridgeFactory, ThingBuilderFactory thingFactory, ChannelBuilderFactory channelFactory) {
    this.bridgeFactory = bridgeFactory;
    this.thingFactory = thingFactory;
    this.channelFactory = channelFactory;
  }

  @Override
  public String getId() {
    return "thing4";
  }

  @Override
  public Class<Thing> getType() {
    return Thing.class;
  }

  @Override
  public List<Thing> parse(byte[] contents) {
    return parse(new String(contents, StandardCharsets.UTF_8));
  }

  @Override
  public List<Thing> parse(String contents) {
    ThingsLexer lexer = new ThingsLexer(CharStreams.fromString(contents));
    lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
    ThingsParser parser = new ThingsParser(new CommonTokenStream(lexer));
    parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
    parser.addErrorListener(new ThingsErrorListener());

    List<Thing> things = new ArrayList<>();
    Listener listener = new Listener(bridgeFactory, thingFactory, channelFactory, things::add);
    new ParseTreeWalker().walk(listener, parser.things());

    return things;
  }

  static class StackEntry {
    final ThingUID uid;
    final ThingBuilder builder;

    StackEntry(ThingUID uid, ThingBuilder builder) {
      this.uid = uid;
      this.builder = builder;
    }
  }

  static class Listener extends ThingsBaseListener {

    private final BridgeBuilderFactory bridgeFactory;
    private final ThingBuilderFactory thingFactory;
    private final ChannelBuilderFactory channelFactory;

    private final Deque<StackEntry> stack = new ArrayDeque<>();
    private final Consumer<Thing> consumer;

    public Listener(BridgeBuilderFactory bridgeFactory, ThingBuilderFactory thingFactory, ChannelBuilderFactory channelFactory, Consumer<Thing> consumer) {
      this.bridgeFactory = bridgeFactory;
      this.thingFactory = thingFactory;
      this.channelFactory = channelFactory;
      this.consumer = consumer;
    }

    @Override
    public void enterModelThing(ModelThingContext ctx) {
      ThingUID parent = resolveParent();
      if (parent != null) {
        throw new IllegalArgumentException("Thing which defines type can't be nested");
      }

      String[] uid = uid(ctx.uid());
      ThingUID thingId = new ThingUID(ctx.uid().getText());
      ThingTypeUID thingType = new ThingTypeUID(uid[0], uid[1]);
      ThingBuilder thingBuilder = thingFactory.create(thingType, thingId);

      if (ctx.parent != null) {
        parent = new ThingUID(ctx.parent.id.getText());
      }
      populate(thingBuilder, parent, ctx.label, ctx.thing.location, ctx.thing.properties);
      stack.addLast(new StackEntry(thingId, thingBuilder));
    }

    @Override
    public void exitModelThing(ModelThingContext ctx) {
      consumer.accept(stack.removeLast().builder.build());
    }

    @Override
    public void enterNestedThing(NestedThingContext ctx) {
      ThingUID parent = resolveParent();
      if (parent == null) {
        throw new IllegalArgumentException("Nested Thing must reside within Bridge");
      }

      ThingTypeUID thingType = new ThingTypeUID(parent.getBindingId(), ctx.thingTypeId.getText());
      ThingUID thingId = new ThingUID(thingType, parent, ctx.id.getText());
      ThingBuilder thingBuilder = thingFactory.create(thingType, thingId);

      populate(thingBuilder, parent, ctx.label, ctx.thing.location, ctx.thing.properties);
      stack.addLast(new StackEntry(thingId, thingBuilder));
    }

    @Override
    public void exitNestedThing(NestedThingContext ctx) {
      consumer.accept(stack.removeLast().builder.build());
    }

    @Override
    public void enterModelBridge(ModelBridgeContext ctx) {
      ThingUID parent = resolveParent();
      if (parent != null) {
        throw new IllegalArgumentException("Thing which defines type can't be nested");
      }

      String[] uid = uid(ctx.uid());
      ThingUID bridgeId = new ThingUID(ctx.uid().getText());
      ThingTypeUID thingType = new ThingTypeUID(uid[0], uid[1]);
      BridgeBuilder bridgeBuilder = bridgeFactory.create(thingType, bridgeId);

      if (ctx.parent != null) {
        parent = new ThingUID(ctx.parent.id.getText());
      }
      populate(bridgeBuilder, parent, ctx.label, ctx.bridge.location, ctx.bridge.properties);
      stack.addLast(new StackEntry(bridgeId, bridgeBuilder));
    }

    @Override
    public void exitModelBridge(ModelBridgeContext ctx) {
      consumer.accept(stack.removeLast().builder.build());
    }

    @Override
    public void enterNestedBridge(NestedBridgeContext ctx) {
      ThingUID parent = resolveParent();
      if (parent == null) {
        throw new IllegalArgumentException("Nested Bridge must reside within Bridge");
      }

      ThingTypeUID thingType = new ThingTypeUID(parent.getBindingId(), ctx.thingTypeId.getText());
      ThingUID bridgeId = new ThingUID(thingType, parent, ctx.id.getText());
      BridgeBuilder bridgeBuilder = bridgeFactory.create(thingType, bridgeId);

      populate(bridgeBuilder, parent, ctx.label, ctx.bridge.location, ctx.bridge.properties);
      stack.addLast(new StackEntry(bridgeId, bridgeBuilder));
    }

    @Override
    public void exitNestedBridge(NestedBridgeContext ctx) {
      consumer.accept(stack.removeLast().builder.build());
    }

    private ThingUID resolveParent() {
      if (stack.isEmpty()) {
        return null;
      }
      return stack.getLast().uid;
    }

    @Override
    public void enterModelChannel(ModelChannelContext ctx) {
      String label = ctx.label != null ? ctx.label.getText() : "";
      ChannelKind channelKind = ctx.channelDeclaration() == null ? ChannelKind.STATE :
        ChannelKind.parse(ctx.channelDeclaration().channelKind.getText());
      String channelType = ctx.channelDeclaration() == null ? ctx.channelReference().channelType.getText() :
        ctx.channelDeclaration().channelKind.getText();
      String itemType = ctx.channelDeclaration() == null ? null :
        ctx.channelDeclaration().itemType.getText();

      final Configuration configuration = createConfiguration(ctx.properties);

      AutoUpdatePolicy autoUpdatePolicy = null;
      String id = stack.element().uid + ":" + ctx.id.getText();
      ChannelTypeUID channelTypeUID = new ChannelTypeUID(stack.element().uid.getBindingId(), channelType);
      ChannelBuilder channelBuilder = channelFactory.create(id, itemType)
        .withKind(channelKind)
        .withConfiguration(configuration)
        .withType(channelTypeUID)
        .withLabel(label)
        .withAutoUpdatePolicy(autoUpdatePolicy);
      Channel channel = channelBuilder.build();

      stack.element().builder.withChannel(channel);
    }

    private void populate(ThingBuilder bridgeBuilder, ThingUID parent, Token label, Token location, ModelPropertiesContext properties) {
      if (parent != null) {
        bridgeBuilder.withBridge(parent);
      }
      if (label != null) {
        bridgeBuilder.withLabel(label.getText());
      }
      if (location != null) {
        bridgeBuilder.withLocation(location.getText());
      }
      if (properties != null) {
        bridgeBuilder.withConfiguration(createConfiguration(properties));
      }
    }

    private static Configuration createConfiguration(ModelPropertiesContext properties) {
      Map<String, Object> config = new LinkedHashMap<>();
      for (ModelPropertyContext propertyContext : properties.modelProperty()) {
        config.put(propertyContext.key.getText(), value(propertyContext.value));
      }

      return new Configuration(config);
    }

    private static Object value(ValueTypeContext value) {
      if (value.BOOLEAN() != null) {
        return Boolean.parseBoolean(value.BOOLEAN().getText());
      } else if (value.STRING() != null) {
        return value.STRING().getText();
      } else if (value.NUMBER() != null) {
        return new BigDecimal(value.NUMBER().getText());
      }

      throw new IllegalArgumentException("Unrecognized value type: " + value.getText());
    }

    private static String[] uid(List<UidSegmentContext> segments) {
      return segments.stream()
        .map(UidSegmentContext::getText)
        .toArray(String[]::new);
    }

    private static String[] uid(UidContext uidContext) {
      return uid(uidContext.uidSegment());
    }
  }


}
