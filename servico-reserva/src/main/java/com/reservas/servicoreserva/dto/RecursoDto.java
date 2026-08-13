package com.reservas.servicoreserva.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecursoDto(String id, String nome, boolean disponivel) {
}
