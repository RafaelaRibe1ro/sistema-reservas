package com.reservas.servicorecurso.dto;

import com.reservas.servicorecurso.document.TipoRecurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Map;

public record RecursoRequest(
		@NotBlank(message = "nome e obrigatorio") String nome,
		@NotNull(message = "tipo e obrigatorio") TipoRecurso tipo,
		@Positive(message = "capacidade deve ser positiva") Integer capacidade,
		Map<String, Object> atributos,
		Boolean disponivel
) {
}
