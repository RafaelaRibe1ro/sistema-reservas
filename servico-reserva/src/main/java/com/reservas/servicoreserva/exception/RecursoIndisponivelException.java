package com.reservas.servicoreserva.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/** Disparada pelo fallback do Circuit Breaker quando o servico-recurso esta fora do ar ou muito lento. */
public class RecursoIndisponivelException extends ErrorResponseException {
	public RecursoIndisponivelException(String message) {
		super(HttpStatus.SERVICE_UNAVAILABLE, ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, message), null);
	}
}
