package com.reservas.servicorecurso.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * Documento MongoDB: cada tipo de recurso (SALA, QUADRA, MESA, QUARTO) tem um
 * conjunto diferente de atributos, por isso "atributos" e um mapa livre em vez
 * de colunas fixas - e o motivo pelo qual esse servico usa um banco de documentos.
 */
@Document(collection = "recursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recurso {

	@Id
	private String id;

	private String nome;

	private TipoRecurso tipo;

	private Integer capacidade;

	private Map<String, Object> atributos;

	@Builder.Default
	private boolean disponivel = true;

	private Instant criadoEm;
}
