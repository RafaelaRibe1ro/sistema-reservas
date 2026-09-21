package com.reservas.servicoauth.dto;

import com.reservas.servicoauth.entity.Credencial;

import java.time.LocalDateTime;

public record CredencialResponse(
		Long id,
		String email,
		LocalDateTime criadoEm
) {
	public static CredencialResponse fromEntity(Credencial credencial) {
		return new CredencialResponse(credencial.getId(), credencial.getEmail(), credencial.getCriadoEm());
	}
}
