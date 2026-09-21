package com.reservas.servicoreserva.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final SecretKey chave;
	private final ObjectMapper objectMapper;

	public JwtAuthFilter(@Value("${jwt.secret}") String secret, ObjectMapper objectMapper) {
		this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.objectMapper = objectMapper;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return request.getRequestURI().startsWith("/actuator");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			rejeitar(response, "token de acesso ausente. Informe o header Authorization: Bearer <token>");
			return;
		}
		String token = header.substring("Bearer ".length());
		try {
			Jwts.parser().verifyWith(chave).build().parseSignedClaims(token);
		} catch (JwtException | IllegalArgumentException e) {
			rejeitar(response, "token de acesso invalido ou expirado");
			return;
		}
		filterChain.doFilter(request, response);
	}

	private void rejeitar(HttpServletResponse response, String detalhe) throws IOException {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, detalhe);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		objectMapper.writeValue(response.getWriter(), problemDetail);
	}
}
