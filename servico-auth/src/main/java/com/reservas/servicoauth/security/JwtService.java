package com.reservas.servicoauth.security;

import com.reservas.servicoauth.exception.TokenInvalidoException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService {

	private static final String CLAIM_TIPO = "tipo";
	private static final String TIPO_ACCESS = "access";
	private static final String TIPO_REFRESH = "refresh";

	private final SecretKey chave;
	private final long accessTokenExpirationMs;
	private final long refreshTokenExpirationMs;

	public JwtService(@Value("${jwt.secret}") String secret,
			@Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
			@Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
		this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTokenExpirationMs = accessTokenExpirationMs;
		this.refreshTokenExpirationMs = refreshTokenExpirationMs;
	}

	public String gerarAccessToken(String email) {
		return gerarToken(email, TIPO_ACCESS, accessTokenExpirationMs);
	}

	public String gerarRefreshToken(String email) {
		return gerarToken(email, TIPO_REFRESH, refreshTokenExpirationMs);
	}

	public long accessTokenExpirationSegundos() {
		return accessTokenExpirationMs / 1000;
	}

	public String extrairEmailDoRefreshToken(String refreshToken) {
		Claims claims = parseClaims(refreshToken, "refresh token invalido ou expirado");
		if (!TIPO_REFRESH.equals(claims.get(CLAIM_TIPO, String.class))) {
			throw new TokenInvalidoException("token informado nao e um refresh token");
		}
		return claims.getSubject();
	}

	private String gerarToken(String email, String tipo, long expirationMs) {
		Date agora = new Date();
		Date expiracao = new Date(agora.getTime() + expirationMs);
		return Jwts.builder()
				.subject(email)
				.claim(CLAIM_TIPO, tipo)
				.issuedAt(agora)
				.expiration(expiracao)
				.signWith(chave)
				.compact();
	}

	private Claims parseClaims(String token, String mensagemErro) {
		try {
			return Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).getPayload();
		} catch (JwtException | IllegalArgumentException e) {
			throw new TokenInvalidoException(mensagemErro);
		}
	}
}
