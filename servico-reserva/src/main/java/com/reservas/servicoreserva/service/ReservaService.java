package com.reservas.servicoreserva.service;

import com.reservas.servicoreserva.client.RecursoClient;
import com.reservas.servicoreserva.client.UsuarioClient;
import com.reservas.servicoreserva.dto.ReservaRequest;
import com.reservas.servicoreserva.dto.ReservaResponse;
import com.reservas.servicoreserva.dto.RecursoDto;
import com.reservas.servicoreserva.entity.Reserva;
import com.reservas.servicoreserva.entity.StatusReserva;
import com.reservas.servicoreserva.exception.ConflitoDeReservaException;
import com.reservas.servicoreserva.exception.RecursoNaoEncontradoException;
import com.reservas.servicoreserva.exception.ReservaNaoEncontradaException;
import com.reservas.servicoreserva.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

	private final ReservaRepository reservaRepository;
	private final UsuarioClient usuarioClient;
	private final RecursoClient recursoClient;

	@Transactional
	public ReservaResponse criar(ReservaRequest request) {
		usuarioClient.buscarUsuario(request.usuarioId());

		RecursoDto recurso = recursoClient.buscarRecurso(request.recursoId());
		if (!recurso.disponivel()) {
			throw new RecursoNaoEncontradoException("Recurso " + request.recursoId() + " nao esta disponivel para reserva");
		}

		if (reservaRepository.existsConflito(request.recursoId(), request.dataInicio(), request.dataFim())) {
			throw new ConflitoDeReservaException(
					"Recurso " + request.recursoId() + " ja possui reserva no periodo informado");
		}

		Reserva reserva = Reserva.builder()
				.usuarioId(request.usuarioId())
				.recursoId(request.recursoId())
				.dataInicio(request.dataInicio())
				.dataFim(request.dataFim())
				.status(StatusReserva.CONFIRMADA)
				.build();
		return ReservaResponse.fromEntity(reservaRepository.save(reserva));
	}

	@Transactional(readOnly = true)
	public ReservaResponse buscarPorId(Long id) {
		return ReservaResponse.fromEntity(buscarEntidade(id));
	}

	@Transactional(readOnly = true)
	public List<ReservaResponse> listarPorUsuario(Long usuarioId) {
		List<Reserva> reservas = usuarioId == null ? reservaRepository.findAll() : reservaRepository.findByUsuarioId(usuarioId);
		return reservas.stream().map(ReservaResponse::fromEntity).toList();
	}

	@Transactional
	public void cancelar(Long id) {
		Reserva reserva = buscarEntidade(id);
		reserva.setStatus(StatusReserva.CANCELADA);
		reservaRepository.save(reserva);
	}

	private Reserva buscarEntidade(Long id) {
		return reservaRepository.findById(id)
				.orElseThrow(() -> new ReservaNaoEncontradaException("Reserva nao encontrada: " + id));
	}
}
