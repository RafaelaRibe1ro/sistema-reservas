package com.reservas.servicousuario.controller;

import com.reservas.servicousuario.dto.UsuarioRequest;
import com.reservas.servicousuario.dto.UsuarioResponse;
import com.reservas.servicousuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

	private final UsuarioService usuarioService;

	@PostMapping
	public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) {
		UsuarioResponse response = usuarioService.criar(request);
		return ResponseEntity.created(URI.create("/usuarios/" + response.id())).body(response);
	}

	@GetMapping
	public List<UsuarioResponse> listar() {
		return usuarioService.listar();
	}

	@GetMapping("/{id}")
	public UsuarioResponse buscarPorId(@PathVariable Long id) {
		return usuarioService.buscarPorId(id);
	}

	@PutMapping("/{id}")
	public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
		return usuarioService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(@PathVariable Long id) {
		usuarioService.remover(id);
		return ResponseEntity.noContent().build();
	}
}
