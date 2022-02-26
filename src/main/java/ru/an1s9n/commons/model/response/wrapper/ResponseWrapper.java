package ru.an1s9n.commons.model.response.wrapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Data
@JsonDeserialize(using = ResponseWrapper.ResponseWrapperDeserializer.class)
public class ResponseWrapper<T> {

  private static final String SIGNATURE_STR = "Signature";

  private static final Pattern SIGNATURE_PATTERN = Pattern.compile(SIGNATURE_STR + "=\"(.+?)\"");

  private final T response;
  private final String rawResponse;
  private final String signature;
  private final String signingBase;

  @Slf4j
  public static class ResponseWrapperDeserializer extends JsonDeserializer<ResponseWrapper<?>> implements ContextualDeserializer {

    private JavaType valueType;

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctx, BeanProperty beanProperty) {
      var deserializer = new ResponseWrapperDeserializer();
      deserializer.valueType = ctx.getContextualType().containedType(0);
      return deserializer;
    }

    @Override
    public ResponseWrapper<?> deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
      var rawResponse = extractRawResponse(jsonParser.getCurrentLocation().contentReference().getRawContent());
      var response = ctx.readValue(jsonParser, valueType);
      var signatureMatcher = SIGNATURE_PATTERN.matcher(rawResponse);
      return signatureMatcher.find() ?
        new ResponseWrapper<>(response, rawResponse, signatureMatcher.group(1), signatureMatcher.replaceAll(SIGNATURE_STR + "=\"\"")) :
        new ResponseWrapper<>(response, rawResponse, "", "");
    }

    private String extractRawResponse(Object rawContent) throws IOException {
      if (rawContent instanceof Reader rawContentReader) {
        rawContentReader.reset();
        return IOUtils.toString(rawContentReader);
      } else if (rawContent instanceof InputStream rawContentInputStream) {
        rawContentInputStream.reset();
        return IOUtils.toString(rawContentInputStream, StandardCharsets.UTF_8);
      } else {
        log.warn("extractRawResponse: can not extract raw response from {}", rawContent);
        return "";
      }
    }

  }

}
