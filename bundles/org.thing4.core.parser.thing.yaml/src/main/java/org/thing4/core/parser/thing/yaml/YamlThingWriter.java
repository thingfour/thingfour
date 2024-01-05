package org.thing4.core.parser.thing.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.util.List;
import org.openhab.core.thing.Thing;
import org.thing4.core.parser.Writer;
import org.thing4.core.parser.thing.yaml.YamlThingParser.Things;
import org.thing4.core.parser.thing.yaml.module.ThingModule;

public class YamlThingWriter implements Writer<Thing> {

  private final ObjectMapper mapper;

  public YamlThingWriter() {
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
  public byte[] write(List<Thing> elements) {;
    try {
      return mapper.writer().writeValueAsBytes(new Things(elements));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
