package com.reservas.servicoauth.repository;

import com.reservas.servicoauth.entity.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredencialRepository extends JpaRepository<Credencial, Long> {

	Optional<Credencial> findByEmail(String email);

	boolean existsByEmail(String email);
}
