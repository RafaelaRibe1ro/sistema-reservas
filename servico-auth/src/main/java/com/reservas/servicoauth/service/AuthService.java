package com.reservas.servicoauth.service;

import com.reservas.servicoauth.dto.CredencialResponse;
import com.reservas.servicoauth.dto.LoginRequest;
import com.reservas.servicoauth.dto.RegistroRequest;
import com.reservas.servicoauth.dto.TokenResponse;
import com.reservas.servicoauth.entity.Credencial;
import com.reservas.servicoauth.exception.CredenciaisInvalidasException;
import com.reservas.servicoauth.exception.EmailJaCadastradoException;
import com.reservas.servicoauth.repository.CredencialRepository;
import com.reservas.servicoauth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final CredencialRepository credencialRepository;
	private final JwtService jwtService;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Transactional
	public CredencialResponse registrar(RegistroRequest request) {
		if (credencialRepository.existsByEmail(request.email())) {
			throw new EmailJaCadastradoException("Ja existe uma credencial com o email " + request.email());
		}
		Credencial credencial = Credencial.builder()
				.email(request.email())
				.senhaHash(passwordEncoder.encode(request.senha()))
				.build();
		return CredencialResponse.fromEntity(credencialRepository.save(credencial));
	}

	@Transactional(readOnly = true)
	public TokenResponse login(LoginRequest request) {
		Credencial credencial = credencialRepository.findByEmail(request.email())
				.orElseThrow(() -> new CredenciaisInvalidasException("email ou senha invalidos"));
		if (!passwordEncoder.matches(request.senha(), credencial.getSenhaHash())) {
			throw new CredenciaisInvalidasException("email ou senha invalidos");
		}
		return gerarTokens(credencial.getEmail());
	}

	@Transactional(readOnly = true)
	public TokenResponse refresh(String refreshToken) {
		String email = jwtService.extrairEmailDoRefreshToken(refreshToken);
		credencialRepository.findByEmail(email)
				.orElseThrow(() -> new CredenciaisInvalidasException("credencial nao encontrada para o token informado"));
		return gerarTokens(email);
	}

	private TokenResponse gerarTokens(String email) {
		String accessToken = jwtService.gerarAccessToken(email);
		String refreshToken = jwtService.gerarRefreshToken(email);
		return TokenResponse.of(accessToken, refreshToken, jwtService.accessTokenExpirationSegundos());
	}
}
