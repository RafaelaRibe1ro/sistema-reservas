package com.reservas.servicoreserva.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/** o recursoId informado nao existe no servico-recurso */
public class RecursoNaoEncontradoException extends ErrorResponseException {
	public RecursoNaoEncontradoException(String message) {
		super(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message), null);
	}
}
