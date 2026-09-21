package com.reservas.servicoauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "credenciais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credencial {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(name = "senha_hash", nullable = false)
	private String senhaHash;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime criadoEm;

	@PrePersist
	public void prePersist() {
		this.criadoEm = LocalDateTime.now();
	}
}
