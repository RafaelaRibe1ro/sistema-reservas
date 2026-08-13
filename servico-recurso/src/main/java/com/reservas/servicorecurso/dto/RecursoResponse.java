package com.reservas.servicorecurso.dto;

import com.reservas.servicorecurso.document.Recurso;
import com.reservas.servicorecurso.document.TipoRecurso;

import java.time.Instant;
import java.util.Map;

public record RecursoResponse(
		String id,
		String nome,
		TipoRecurso tipo,
		Integer capacidade,
		Map<String, Object> atributos,
		boolean disponivel,
		Instant criadoEm
) {
	public static RecursoResponse fromDocument(Recurso recurso) {
		return new RecursoResponse(
				recurso.getId(),
				recurso.getNome(),
				recurso.getTipo(),
				recurso.getCapacidade(),
				recurso.getAtributos(),
				recurso.isDisponivel(),
				recurso.getCriadoEm()
		);
	}
}
