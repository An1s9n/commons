package ru.an1s9n.commons.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectMapperUtils {

  private static final ObjectMapper XML_MAPPER = XmlMapper.builder()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
    .configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
    .serializationInclusion(JsonInclude.Include.NON_NULL)
    .build();

  public static ObjectMapper getXmlMapper() {
    return XML_MAPPER;
  }

}
