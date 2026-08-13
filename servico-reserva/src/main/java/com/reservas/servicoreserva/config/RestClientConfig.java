package com.reservas.servicoreserva.config;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Dois beans de RestClient.Builder de proposito: o plain/@Primary e usado
 * internamente pelo proprio Eureka client (que tambem procura um RestClient.Builder
 * no contexto para falar com localhost:8761) - se ele pegasse o @LoadBalanced,
 * tentaria balancear "localhost" como se fosse um service-id e falharia o registro.
 * O @LoadBalanced (com timeout de 2s) e o que os clients de negocio usam de fato,
 * injetado explicitamente via @Qualifier.
 */
@Configuration
public class RestClientConfig {

	@Bean
	@Primary
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}

	@Bean
	@LoadBalanced
	public RestClient.Builder loadBalancedRestClientBuilder() {
		HttpClientSettings settings = HttpClientSettings.defaults()
				.withConnectTimeout(Duration.ofSeconds(2))
				.withReadTimeout(Duration.ofSeconds(2));
		ClientHttpRequestFactory factory = ClientHttpRequestFactoryBuilder.detect().build(settings);
		return RestClient.builder().requestFactory(factory);
	}
}
