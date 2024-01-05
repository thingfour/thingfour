package org.thing4.core.parser.thing.yaml.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.function.Function;
import org.openhab.core.thing.UID;

public class UIDHandler {

  public static <T extends UID> JsonDeserializer<T> deserializer(Class<T> type, Function<String, T> creator) {
    return new UIDDeserializer<>(creator);
  }

  public static <T extends UID> JsonSerializer<T> serializer(Class<T> type) {
    return new UIDSerializer<>(type);
  }

  static class UIDDeserializer<T extends UID> extends StdDeserializer<T> {

    private final Function<String, T> creator;

    public UIDDeserializer(Function<String, T> creator) {
      super(UID.class);
      this.creator = creator;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return creator.apply(p.nextTextValue());
    }
  }

  static class UIDSerializer<T extends UID> extends StdSerializer<T> {

    public UIDSerializer(Class<T> type) {
      super(type);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeString(value.getAsString());
    }

  }

}
