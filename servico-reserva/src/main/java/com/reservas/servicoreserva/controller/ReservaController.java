package com.reservas.servicoreserva.controller;

import com.reservas.servicoreserva.dto.ReservaRequest;
import com.reservas.servicoreserva.dto.ReservaResponse;
import com.reservas.servicoreserva.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

	private final ReservaService reservaService;

	@PostMapping
	public ResponseEntity<ReservaResponse> criar(@Valid @RequestBody ReservaRequest request) {
		ReservaResponse response = reservaService.criar(request);
		return ResponseEntity.created(URI.create("/reservas/" + response.id())).body(response);
	}

	@GetMapping("/{id}")
	public ReservaResponse buscarPorId(@PathVariable Long id) {
		return reservaService.buscarPorId(id);
	}

	@GetMapping
	public List<ReservaResponse> listar(@RequestParam(required = false) Long usuarioId) {
		return reservaService.listarPorUsuario(usuarioId);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> cancelar(@PathVariable Long id) {
		reservaService.cancelar(id);
		return ResponseEntity.noContent().build();
	}
}
