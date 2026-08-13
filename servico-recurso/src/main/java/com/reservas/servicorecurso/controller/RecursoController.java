package com.reservas.servicorecurso.controller;

import com.reservas.servicorecurso.document.TipoRecurso;
import com.reservas.servicorecurso.dto.RecursoRequest;
import com.reservas.servicorecurso.dto.RecursoResponse;
import com.reservas.servicorecurso.service.RecursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/recursos")
@RequiredArgsConstructor
public class RecursoController {

	private final RecursoService recursoService;

	@PostMapping
	public ResponseEntity<RecursoResponse> criar(@Valid @RequestBody RecursoRequest request) {
		RecursoResponse response = recursoService.criar(request);
		return ResponseEntity.created(URI.create("/recursos/" + response.id())).body(response);
	}

	@GetMapping
	public List<RecursoResponse> listar(@RequestParam(required = false) TipoRecurso tipo) {
		return recursoService.listar(tipo);
	}

	@GetMapping("/{id}")
	public RecursoResponse buscarPorId(@PathVariable String id) {
		return recursoService.buscarPorId(id);
	}

	@PutMapping("/{id}")
	public RecursoResponse atualizar(@PathVariable String id, @Valid @RequestBody RecursoRequest request) {
		return recursoService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(@PathVariable String id) {
		recursoService.remover(id);
		return ResponseEntity.noContent().build();
	}
}
