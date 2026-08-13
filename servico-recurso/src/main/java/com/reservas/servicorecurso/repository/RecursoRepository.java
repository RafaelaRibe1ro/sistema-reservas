package com.reservas.servicorecurso.repository;

import com.reservas.servicorecurso.document.Recurso;
import com.reservas.servicorecurso.document.TipoRecurso;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RecursoRepository extends MongoRepository<Recurso, String> {

	List<Recurso> findByTipo(TipoRecurso tipo);
}
