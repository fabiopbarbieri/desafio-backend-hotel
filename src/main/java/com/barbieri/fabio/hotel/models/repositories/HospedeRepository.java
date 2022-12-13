package com.barbieri.fabio.hotel.models.repositories;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.barbieri.fabio.hotel.models.entities.Hospede;

public interface HospedeRepository extends CrudRepository<Hospede, String> {

	@Override
	@Transactional
	void deleteById(String id);

	Iterable<Hospede> findByDocumentoContaining(String documento);

	Iterable<Hospede> findByDocumentoOrNomeOrTelefone(String documento, String nome, String telefone);

	Iterable<Hospede> findByNomeContainingIgnoreCaseAndTelefoneContaining(String nome, String telefone);

	Iterable<Hospede> findByNomeContainingIgnoreCase(String nome);

	Iterable<Hospede> findByNomeIgnoreCase(String nome);

	Iterable<Hospede> findByTelefone(String telefone);

	Iterable<Hospede> findByTelefoneContaining(String telefone);

	@Query(value = "SELECT h.documento, h.nome, h.telefone FROM hospedes h "
			+ " INNER JOIN registro_hospedagem rh ON h.documento = rh.documento_hospede "
			+ " WHERE NOW()::::TIMESTAMP(0) BETWEEN rh.data_entrada AND rh.data_saida "
			+ " GROUP BY h.documento, h.nome, h.telefone "
			+ " ORDER BY h.documento", nativeQuery = true)
	Iterable<Hospede> findHospedesHospedados();

	@Query(value = "SELECT h.documento, h.nome, h.telefone FROM hospedes h "
			+ " INNER JOIN registro_hospedagem rh ON h.documento = rh.documento_hospede "
			+ " WHERE rh.data_saida < NOW()::::TIMESTAMP(0) " + " GROUP BY h.documento, h.nome, h.telefone "
			+ " HAVING count(rh) = (SELECT COUNT(rh2) FROM registro_hospedagem rh2 WHERE rh2.documento_hospede = h.documento AND NOW()::::TIMESTAMP(0) NOT BETWEEN rh2.data_entrada AND rh2.data_saida) "
			+ " ORDER BY h.documento", nativeQuery = true)
	Iterable<Hospede> findHospedesNaoHospedados();
	
	@Query(value="SELECT h.documento, h.nome, h.telefone, gh.documento, gh.valor_ultima_hospedagem, gh.valor_total_hospedagens "
			+ " FROM hospedes h "
			+ " INNER JOIN gastos_hospedes gh ON h.documento = gh.documento "
			+ " WHERE h.documento = ?1", nativeQuery = true)
	Hospede findHospedeWithGastos(String documento);
	
}
