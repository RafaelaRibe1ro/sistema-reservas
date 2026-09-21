package com.reservas.servicoauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class EmailJaCadastradoException extends ErrorResponseException {
	public EmailJaCadastradoException(String message) {
		super(HttpStatus.CONFLICT, ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, message), null);
	}
}
