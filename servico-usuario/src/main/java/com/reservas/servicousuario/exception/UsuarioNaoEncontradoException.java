package com.reservas.servicousuario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class UsuarioNaoEncontradoException extends ErrorResponseException {
	public UsuarioNaoEncontradoException(String message) {
		super(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message), null);
	}
}
