package com.reservas.servicoreserva.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.reservas.servicoreserva.dto.RecursoDto;
import com.reservas.servicoreserva.exception.RecursoIndisponivelException;
import com.reservas.servicoreserva.exception.RecursoNaoEncontradoException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

/**
 * servico-reserva -> servico-recurso: risco = servico-recurso fora do ar ou lento.
 * Estrategia de resiliencia: Timeout + Circuit Breaker + Fallback.
 * O fallback so e acionado por falhas de infraestrutura (timeout, conexao recusada,
 * 5xx); um 404 legitimo (recurso inexistente) e tratado como falha de negocio e
 * ignorado pelo circuit breaker (ver resilience4j.circuitbreaker.instances.servicoRecurso.ignore-exceptions).
 */
@Component
@Slf4j
public class RecursoClient {

	private final RestClient.Builder loadBalancedRestClientBuilder;

	public RecursoClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder loadBalancedRestClientBuilder) {
		this.loadBalancedRestClientBuilder = loadBalancedRestClientBuilder;
	}

	@CircuitBreaker(name = "servicoRecurso", fallbackMethod = "buscarRecursoFallback")
	public RecursoDto buscarRecurso(String recursoId) {
		try {
			return loadBalancedRestClientBuilder.build()
					.get()
					.uri("http://servico-recurso/recursos/{id}", recursoId)
					.retrieve()
					.body(RecursoDto.class);
		} catch (HttpClientErrorException.NotFound e) {
			throw new RecursoNaoEncontradoException("Recurso nao encontrado: " + recursoId);
		}
	}

	private RecursoDto buscarRecursoFallback(String recursoId, RecursoNaoEncontradoException e) {
		throw e;
	}

	private RecursoDto buscarRecursoFallback(String recursoId, Throwable t) {
		log.warn("servico-recurso indisponivel ao consultar recurso {}: {}", recursoId, t.toString());
		throw new RecursoIndisponivelException(
				"Servico de recursos indisponivel no momento. Tente novamente mais tarde.");
	}
}
