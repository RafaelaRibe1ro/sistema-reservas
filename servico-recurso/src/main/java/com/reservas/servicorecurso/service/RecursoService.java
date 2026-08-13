package com.reservas.servicorecurso.service;

import com.reservas.servicorecurso.document.Recurso;
import com.reservas.servicorecurso.document.TipoRecurso;
import com.reservas.servicorecurso.dto.RecursoRequest;
import com.reservas.servicorecurso.dto.RecursoResponse;
import com.reservas.servicorecurso.exception.RecursoNaoEncontradoException;
import com.reservas.servicorecurso.repository.RecursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecursoService {

	private final RecursoRepository recursoRepository;

	public RecursoResponse criar(RecursoRequest request) {
		Recurso recurso = Recurso.builder()
				.nome(request.nome())
				.tipo(request.tipo())
				.capacidade(request.capacidade())
				.atributos(request.atributos())
				.disponivel(request.disponivel() == null || request.disponivel())
				.criadoEm(Instant.now())
				.build();
		return RecursoResponse.fromDocument(recursoRepository.save(recurso));
	}

	public List<RecursoResponse> listar(TipoRecurso tipo) {
		List<Recurso> recursos = tipo == null
				? recursoRepository.findAll()
				: recursoRepository.findByTipo(tipo);
		return recursos.stream().map(RecursoResponse::fromDocument).toList();
	}

	public RecursoResponse buscarPorId(String id) {
		return RecursoResponse.fromDocument(buscarEntidade(id));
	}

	public RecursoResponse atualizar(String id, RecursoRequest request) {
		Recurso recurso = buscarEntidade(id);
		recurso.setNome(request.nome());
		recurso.setTipo(request.tipo());
		recurso.setCapacidade(request.capacidade());
		recurso.setAtributos(request.atributos());
		if (request.disponivel() != null) {
			recurso.setDisponivel(request.disponivel());
		}
		return RecursoResponse.fromDocument(recursoRepository.save(recurso));
	}

	public void remover(String id) {
		recursoRepository.delete(buscarEntidade(id));
	}

	private Recurso buscarEntidade(String id) {
		return recursoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Recurso nao encontrado: " + id));
	}
}
