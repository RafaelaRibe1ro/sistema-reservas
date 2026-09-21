package com.reservas.servicoauth.dto;

public record TokenResponse(
		String accessToken,
		String refreshToken,
		String tipo,
		long expiraEmSegundos
) {
	public static TokenResponse of(String accessToken, String refreshToken, long expiraEmSegundos) {
		return new TokenResponse(accessToken, refreshToken, "Bearer", expiraEmSegundos);
	}
}
