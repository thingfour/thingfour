package org.thing4.core.parser.thing.yaml;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openhab.core.thing.Thing;
import org.thing4.core.parser.Parser;
import org.thing4.core.parser.thing.yaml.module.ThingModule;

public class YamlThingParser implements Parser<Thing> {

  private final ObjectMapper mapper;

  public YamlThingParser() {
    mapper = new ObjectMapper(new YAMLFactory());
    mapper.registerModule(new ThingModule(new InjectableValues.Std()));
  }

  @Override
  public String getId() {
    return "yaml";
  }

  @Override
  public Class<Thing> getType() {
    return Thing.class;
  }

  @Override
  public List<Thing> parse(byte[] contents) {
    if (contents.length == 0) {
      return new ArrayList<>();
    }

    try {
      Things things = mapper.readerFor(Things.class).readValue(contents);
      if (things != null) {
        return things.things;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new ArrayList<>();
  }

  @Override
  public List<Thing> parse(String contents) {
    return parse(contents.getBytes());
  }

  static class Things {
    private List<Thing> things;

    public Things() {
      this(new ArrayList<>());
    }

    public Things(List<Thing> things) {
      this.things = things;
    }

    public List<Thing> getThings() {
      return things;
    }

    public void setThings(List<Thing> things) {
      this.things = things;
    }
  }
}
