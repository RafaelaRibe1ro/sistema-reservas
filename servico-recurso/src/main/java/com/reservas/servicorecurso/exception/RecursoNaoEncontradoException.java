package com.reservas.servicorecurso.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class RecursoNaoEncontradoException extends ErrorResponseException {
	public RecursoNaoEncontradoException(String message) {
		super(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message), null);
	}
}
