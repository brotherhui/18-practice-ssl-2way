package com.brotherhui.config;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Configuration
//@PropertySource("classpath:application.yml")
public class RestClientSSLConfig {

    @Value("${ssltest.trust-store-password}")
    private String trustStorePassword;
    @Value("${ssltest.trust-store}")
    private Resource trustStore;
	@Value("${ssltest.key-store-password}")
	private String keyStorePassword;
	@Value("${ssltest.key-password}")
	private String keyPassword;
	@Value("${ssltest.key-store}")
	private Resource keyStore; 
  

	
    @Bean
    public RestOperations restOperations(ClientHttpRequestFactory clientHttpRequestFactory) {
        return new RestTemplate(clientHttpRequestFactory);
    }
  
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }
  
    @Bean
    public HttpClient httpClient() throws Exception {
        // Load our keystore and truststore containing certificates that we trust.
        SSLContext sslcontext =
                SSLContexts.custom().loadTrustMaterial(trustStore.getFile(), trustStorePassword.toCharArray())
                        .loadKeyMaterial(keyStore.getFile(), keyStorePassword.toCharArray(),
                                keyPassword.toCharArray())
                        .build();
        SSLConnectionSocketFactory sslConnectionSocketFactory =
                new SSLConnectionSocketFactory(sslcontext, new NoopHostnameVerifier());
        return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
    }
    
  
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
