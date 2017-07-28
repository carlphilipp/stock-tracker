package fr.cph.stock.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Configuration
public class RestConfig {

	@NonNull
	private final AppProperties appProperties;

	@Bean
	public HttpClientConnectionManager httpClientConnectionManager() {
		final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(appProperties.getRest().getClient().getMaxTotalConnections());
		connectionManager.setDefaultMaxPerRoute(appProperties.getRest().getClient().getMaxConnectionsPerRoute());
		return connectionManager;
	}

	@Bean
	public CloseableHttpClient closeableHttpClient() {
		return HttpClientBuilder.create()
			.setConnectionManager(httpClientConnectionManager())
			.setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(appProperties.getRest().getClient().getReadTimeout()).build())
			.build();
	}

	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory() {
		return new HttpComponentsClientHttpRequestFactory(closeableHttpClient());
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
