/*
 * Copyright (C) 2023-2023 ConnectorIO Sp. z o.o.
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
package org.thing4.addons.model.thing.parser;

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
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.builder.BridgeBuilder;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.openhab.core.thing.type.AutoUpdatePolicy;
import org.openhab.core.thing.type.ChannelKind;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.thing4.addons.model.thing.BridgeBuilderFactory;
import org.thing4.addons.model.thing.ChannelBuilderFactory;
import org.thing4.addons.model.thing.ThingBuilderFactory;
import org.thing4.addons.model.thing.ThingsBaseListener;
import org.thing4.addons.model.thing.ThingsLexer;
import org.thing4.addons.model.thing.ThingsParser;
import org.thing4.addons.model.thing.ThingsParser.ModelBridgeContext;
import org.thing4.addons.model.thing.ThingsParser.ModelChannelContext;
import org.thing4.addons.model.thing.ThingsParser.ModelParentContext;
import org.thing4.addons.model.thing.ThingsParser.ModelPropertiesContext;
import org.thing4.addons.model.thing.ThingsParser.ModelPropertyContext;
import org.thing4.addons.model.thing.ThingsParser.ModelThingContext;
import org.thing4.addons.model.thing.ThingsParser.UidContext;
import org.thing4.addons.model.thing.ThingsParser.UidSegmentContext;
import org.thing4.addons.model.thing.ThingsParser.ValueTypeContext;

public class AntlrV4ThingsParser {

  private final BridgeBuilderFactory bridgeFactory;
  private final ThingBuilderFactory thingFactory;

  private final ChannelBuilderFactory channelFactory;

  public AntlrV4ThingsParser(BridgeBuilderFactory bridgeFactory, ThingBuilderFactory thingFactory, ChannelBuilderFactory channelFactory) {
    this.bridgeFactory = bridgeFactory;
    this.thingFactory = thingFactory;
    this.channelFactory = channelFactory;
  }

  public List<Thing> parse(byte[] contents) {
    return parse(new String(contents, StandardCharsets.UTF_8));
  }

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
      ThingUID thingUID = null;
      ThingBuilder thingBuilder = null;
      if (ctx.id != null) {
        String[] uid = uid(ctx.id);
        thingUID = new ThingUID(uid);
        thingBuilder = thingFactory.create(
          uid[0] + ":" + uid[1],
          uid[uid.length - 1]
        );
      }
      if (ctx.thingTypeId != null) {
        thingBuilder = thingFactory.create(
          ctx.thingTypeId.getText(),
          ctx.thingId.getText()
        );
        thingUID = new ThingUID(ctx.thingTypeId.getText(), ctx.thingId.getText());
      }

      populate(thingBuilder, ctx.parent, ctx.label, ctx.location, ctx.properties);

      stack.offer(new StackEntry(thingUID, thingBuilder));
    }

    @Override
    public void exitModelThing(ModelThingContext ctx) {
      consumer.accept(stack.removeLast().builder.build());
    }

    @Override
    public void enterModelBridge(ModelBridgeContext ctx) {
      BridgeBuilder bridgeBuilder = null;
      ThingUID bridgeUID = null;
      if (ctx.id != null) {
        String[] uid = uid(ctx.id);
        bridgeBuilder = bridgeFactory.create(
          uid[0] + ":" + uid[1],
          uid[uid.length - 1]
        );
        bridgeUID = new ThingUID(uid);
      }
      if (ctx.thingTypeId != null) {
        bridgeBuilder = bridgeFactory.create(
          ctx.thingTypeId.getText(),
          ctx.thingId.getText()
        );
        bridgeUID = new ThingUID(ctx.thingTypeId.getText(), ctx.thingId.getText());
      }

      populate(bridgeBuilder, ctx.parent, ctx.label, ctx.location, ctx.properties);
      stack.push(new StackEntry(bridgeUID, bridgeBuilder));
    }

    @Override
    public void exitModelBridge(ModelBridgeContext ctx) {
      consumer.accept(stack.removeLast().builder.build());
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

    private void populate(ThingBuilder bridgeBuilder, ModelParentContext parent, Token label, Token location, ModelPropertiesContext properties) {
      if (parent != null) {
        bridgeBuilder.withBridge(new ThingUID(parent.uid().getText()));
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
