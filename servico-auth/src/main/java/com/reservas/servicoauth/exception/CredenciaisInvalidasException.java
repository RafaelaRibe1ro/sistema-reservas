package com.reservas.servicoauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class CredenciaisInvalidasException extends ErrorResponseException {
	public CredenciaisInvalidasException(String message) {
		super(HttpStatus.UNAUTHORIZED, ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, message), null);
	}
}
