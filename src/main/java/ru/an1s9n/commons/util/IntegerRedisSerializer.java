package ru.an1s9n.commons.util;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.ByteBuffer;

public final class IntegerRedisSerializer implements RedisSerializer<Integer> {

  @Override
  public byte[] serialize(Integer integer) throws SerializationException {
    return integer != null ? ByteBuffer.allocate(4).putInt(integer).array() : null;
  }

  @Override
  public Integer deserialize(byte[] bytes) throws SerializationException {
    return bytes != null ? ByteBuffer.wrap(bytes).getInt() : null;
  }

  @Override
  public Class<?> getTargetType() {
    return Integer.class;
  }
}
