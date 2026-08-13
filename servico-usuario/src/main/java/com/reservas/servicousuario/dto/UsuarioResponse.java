package com.reservas.servicousuario.dto;

import com.reservas.servicousuario.entity.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
		Long id,
		String nome,
		String email,
		String telefone,
		LocalDateTime criadoEm
) {
	public static UsuarioResponse fromEntity(Usuario usuario) {
		return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTelefone(), usuario.getCriadoEm());
	}
}
