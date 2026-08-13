package com.reservas.servicoreserva.client;

import com.reservas.servicoreserva.dto.UsuarioDto;
import com.reservas.servicoreserva.exception.UsuarioNaoEncontradoException;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * servico-reserva -> servico-usuario: risco = falha transitoria de rede/instabilidade.
 * Estrategia de resiliencia: Timeout (RestClientConfig) + Retry (3 tentativas, 500ms).
 */
@Component
public class UsuarioClient {

	private final RestClient.Builder loadBalancedRestClientBuilder;

	public UsuarioClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder loadBalancedRestClientBuilder) {
		this.loadBalancedRestClientBuilder = loadBalancedRestClientBuilder;
	}

	@Retry(name = "servicoUsuario")
	public UsuarioDto buscarUsuario(Long usuarioId) {
		try {
			return loadBalancedRestClientBuilder.build()
					.get()
					.uri("http://servico-usuario/usuarios/{id}", usuarioId)
					.retrieve()
					.body(UsuarioDto.class);
		} catch (HttpClientErrorException.NotFound e) {
			throw new UsuarioNaoEncontradoException("Usuario nao encontrado: " + usuarioId);
		}
	}
}
