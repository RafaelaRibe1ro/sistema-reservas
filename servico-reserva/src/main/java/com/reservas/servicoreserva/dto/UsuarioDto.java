package com.reservas.servicoreserva.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioDto(Long id, String nome, String email) {
}
