package com.reservas.servicoreserva.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservaRequest(
		@NotNull(message = "usuarioId e obrigatorio") Long usuarioId,
		@NotBlank(message = "recursoId e obrigatorio") String recursoId,
		@NotNull(message = "dataInicio e obrigatoria") @Future(message = "dataInicio deve ser no futuro") LocalDateTime dataInicio,
		@NotNull(message = "dataFim e obrigatoria") LocalDateTime dataFim
) {
}
