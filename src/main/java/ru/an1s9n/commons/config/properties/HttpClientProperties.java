package ru.an1s9n.commons.config.properties;

import org.springframework.lang.Nullable;

public interface HttpClientProperties {

  @Nullable
  String getBaseUrl();

  @Nullable
  String getBasicLogin();

  @Nullable
  String getBasicPass();

  int getConnectionRequestTimeout();

  int getConnectTimeout();

  int getReadTimeout();

  int getMaxConn();
}
