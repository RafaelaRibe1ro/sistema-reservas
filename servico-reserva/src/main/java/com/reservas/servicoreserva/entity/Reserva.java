package com.reservas.servicoreserva.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "usuario_id", nullable = false)
	private Long usuarioId;

	@Column(name = "recurso_id", nullable = false)
	private String recursoId;

	@Column(name = "data_inicio", nullable = false)
	private LocalDateTime dataInicio;

	@Column(name = "data_fim", nullable = false)
	private LocalDateTime dataFim;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusReserva status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime criadoEm;

	@PrePersist
	public void prePersist() {
		this.criadoEm = LocalDateTime.now();
	}
}
