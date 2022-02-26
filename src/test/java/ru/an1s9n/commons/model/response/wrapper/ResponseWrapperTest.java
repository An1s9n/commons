package ru.an1s9n.commons.model.response.wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.an1s9n.commons.util.ObjectMapperUtils;

class ResponseWrapperTest {

  private static final String SAMPLE_SIGNATURE = "TOP_secret";
  private static final String SAMPLE_XML = """
    <?xml version='1.0' encoding='UTF-8'?>

    <Pojo someAttrString="An1s9n@yandex.ru" someAttrInt="7" someAttrBoolean="true" Signature="%s">
      <someString>foo-bar</someString>
      <someInt>12</someInt>
      <someBoolean>false</someBoolean>
    </Pojo>
    """.formatted(SAMPLE_SIGNATURE);

  @Test
  void responseWrapperDeserializationTest() throws JsonProcessingException {
    var wrappedPojo = ObjectMapperUtils.getXmlMapper()
      .readValue(SAMPLE_XML, new TypeReference<ResponseWrapper<Pojo>>() {
      });
    Assertions.assertEquals(SAMPLE_XML, wrappedPojo.getRawResponse());
    Assertions.assertEquals(SAMPLE_SIGNATURE, wrappedPojo.getSignature());
    Assertions.assertEquals(SAMPLE_XML.replaceAll(SAMPLE_SIGNATURE, ""), wrappedPojo.getSigningBase());
    var pojo = wrappedPojo.getResponse();
    Assertions.assertEquals("An1s9n@yandex.ru", pojo.getSomeAttrString());
    Assertions.assertEquals(7, pojo.getSomeAttrInt());
    Assertions.assertTrue(pojo.isSomeAttrBoolean());
    Assertions.assertEquals("foo-bar", pojo.getSomeString());
    Assertions.assertEquals(12, pojo.getSomeInt());
    Assertions.assertFalse(pojo.isSomeBoolean());
  }

  @Data
  private static class Pojo {
    @JacksonXmlProperty(isAttribute = true)
    private String someAttrString;
    @JacksonXmlProperty(isAttribute = true)
    private int someAttrInt;
    @JacksonXmlProperty(isAttribute = true)
    private boolean someAttrBoolean;
    private String someString;
    private int someInt;
    private boolean someBoolean;
  }

}

