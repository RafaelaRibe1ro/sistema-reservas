package com.reservas.servicousuario.service;

import com.reservas.servicousuario.dto.UsuarioRequest;
import com.reservas.servicousuario.dto.UsuarioResponse;
import com.reservas.servicousuario.entity.Usuario;
import com.reservas.servicousuario.exception.EmailJaCadastradoException;
import com.reservas.servicousuario.exception.UsuarioNaoEncontradoException;
import com.reservas.servicousuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;

	@Transactional
	public UsuarioResponse criar(UsuarioRequest request) {
		if (usuarioRepository.existsByEmail(request.email())) {
			throw new EmailJaCadastradoException("Ja existe um usuario com o email " + request.email());
		}
		Usuario usuario = Usuario.builder()
				.nome(request.nome())
				.email(request.email())
				.telefone(request.telefone())
				.build();
		return UsuarioResponse.fromEntity(usuarioRepository.save(usuario));
	}

	@Transactional(readOnly = true)
	public List<UsuarioResponse> listar() {
		return usuarioRepository.findAll().stream().map(UsuarioResponse::fromEntity).toList();
	}

	@Transactional(readOnly = true)
	public UsuarioResponse buscarPorId(Long id) {
		return UsuarioResponse.fromEntity(buscarEntidade(id));
	}

	@Transactional
	public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
		Usuario usuario = buscarEntidade(id);
		usuario.setNome(request.nome());
		usuario.setTelefone(request.telefone());
		if (!usuario.getEmail().equals(request.email()) && usuarioRepository.existsByEmail(request.email())) {
			throw new EmailJaCadastradoException("Ja existe um usuario com o email " + request.email());
		}
		usuario.setEmail(request.email());
		return UsuarioResponse.fromEntity(usuarioRepository.save(usuario));
	}

	@Transactional
	public void remover(Long id) {
		Usuario usuario = buscarEntidade(id);
		usuarioRepository.delete(usuario);
	}

	private Usuario buscarEntidade(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario nao encontrado: " + id));
	}
}
