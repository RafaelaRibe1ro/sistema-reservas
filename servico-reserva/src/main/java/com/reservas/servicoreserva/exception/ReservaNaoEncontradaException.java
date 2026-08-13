package com.reservas.servicoreserva.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ReservaNaoEncontradaException extends ErrorResponseException {
	public ReservaNaoEncontradaException(String message) {
		super(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message), null);
	}
}
