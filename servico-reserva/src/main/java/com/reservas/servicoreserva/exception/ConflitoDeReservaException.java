package com.reservas.servicoreserva.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ConflitoDeReservaException extends ErrorResponseException {
	public ConflitoDeReservaException(String message) {
		super(HttpStatus.CONFLICT, ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, message), null);
	}
}
