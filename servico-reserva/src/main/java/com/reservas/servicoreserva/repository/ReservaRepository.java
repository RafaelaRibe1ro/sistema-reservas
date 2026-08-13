package com.reservas.servicoreserva.repository;

import com.reservas.servicoreserva.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

	List<Reserva> findByUsuarioId(Long usuarioId);

	@Query("""
			select case when count(b) > 0 then true else false end
			from Reserva b
			where b.recursoId = :recursoId
			and b.status <> com.reservas.servicoreserva.entity.StatusReserva.CANCELADA
			and b.dataInicio < :dataFim
			and b.dataFim > :dataInicio
			""")
	boolean existsConflito(@Param("recursoId") String recursoId,
							@Param("dataInicio") LocalDateTime dataInicio,
							@Param("dataFim") LocalDateTime dataFim);
}
