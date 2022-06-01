package ru.an1s9n.commons.util;;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;
import ru.an1s9n.commons.config.properties.HttpClientProperties;

import java.net.URI;
import java.util.List;
import java.util.function.BiFunction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RestTemplatePreparator2 {

  public static RestTemplate prepareRestTemplate(
    HttpClientProperties clientProps,
    List<HttpMessageConverter<?>> messageConverters
  ) {
    var restTemplate = new RestTemplate();
    restTemplate.setUriTemplateHandler(prepareUriTemplateHandler(clientProps.getBaseUrl()));
    restTemplate.setRequestFactory(prepareRequestFactory(clientProps));
    restTemplate.setMessageConverters(messageConverters);
    return restTemplate;
  }

  private static UriTemplateHandler prepareUriTemplateHandler(String baseUrl) {
    return baseUrl != null ? new DefaultUriBuilderFactory(baseUrl) : new DefaultUriBuilderFactory();
  }

  private static ClientHttpRequestFactory prepareRequestFactory(HttpClientProperties clientProps) {
    var requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setHttpClient(prepareHttpClient(clientProps));
    requestFactory.setHttpContextFactory(prepareContextFactory(clientProps));
    requestFactory.setConnectionRequestTimeout(clientProps.getConnectionRequestTimeout());
    requestFactory.setConnectTimeout(clientProps.getConnectTimeout());
    requestFactory.setReadTimeout(clientProps.getReadTimeout());
    return requestFactory;
  }

  private static HttpClient prepareHttpClient(HttpClientProperties clientProps) {
    return HttpClientBuilder.create()
      .setMaxConnTotal(clientProps.getMaxConn())
      .setMaxConnPerRoute(clientProps.getMaxConn())
      .build();
  }

  private static BiFunction<HttpMethod, URI, HttpContext> prepareContextFactory(HttpClientProperties clientProps) {
    return (httpMethod, uri) -> {
      var httpClientContext = HttpClientContext.create();
      var basicLogin = clientProps.getBasicLogin();
      var basicPass = clientProps.getBasicPass();
      if(basicLogin != null && basicPass != null) {
        httpClientContext.setCredentialsProvider(prepareCredentialsProvider(basicLogin, basicPass));
        httpClientContext.setAuthCache(prepareAuthCache(uri));
      }
      return httpClientContext;
    };
  }

  private static CredentialsProvider prepareCredentialsProvider(String basicLogin, String basicPass) {
    var credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(basicLogin, basicPass));
    return credentialsProvider;
  }

  private static AuthCache prepareAuthCache(URI uri) {
    var authCache = new BasicAuthCache();
    authCache.put(new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()), new BasicScheme());
    return authCache;
  }

}
