package com.reservas.servicoreserva.dto;

import com.reservas.servicoreserva.entity.Reserva;
import com.reservas.servicoreserva.entity.StatusReserva;

import java.time.LocalDateTime;

public record ReservaResponse(
		Long id,
		Long usuarioId,
		String recursoId,
		LocalDateTime dataInicio,
		LocalDateTime dataFim,
		StatusReserva status,
		LocalDateTime criadoEm
) {
	public static ReservaResponse fromEntity(Reserva reserva) {
		return new ReservaResponse(
				reserva.getId(),
				reserva.getUsuarioId(),
				reserva.getRecursoId(),
				reserva.getDataInicio(),
				reserva.getDataFim(),
				reserva.getStatus(),
				reserva.getCriadoEm()
		);
	}
}
