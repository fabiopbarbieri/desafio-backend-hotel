package com.barbieri.fabio.hotel.models.repositories;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.barbieri.fabio.hotel.models.entities.RegistroHospedagem;

public interface RegistroHospedagemRepository extends CrudRepository<RegistroHospedagem, Integer> {

	@Query(value = "SELECT rh FROM registro_hospedagem rh WHERE rh.documento_hospede = ?1 ORDER BY data_entrada DESC LIMIT 1", nativeQuery = true)
	Optional<RegistroHospedagem> findUltimaHospedagem(String documento);

	@Query(value = "SELECT CASE WHEN COUNT(rh) > 0 THEN true ELSE false END FROM registro_hospedagem rh "
			+ " WHERE rh.documento_hospede = ?1 AND NOW()::::TIMESTAMP(0) BETWEEN rh.data_entrada AND rh.data_saida", nativeQuery = true)
	Optional<Boolean> isHospedado(String documento);

	@Query(value = "SELECT CASE WHEN COUNT(rh) > 0 THEN true ELSE false END FROM registro_hospedagem rh "
			+ " WHERE rh.documento_hospede = ?1 "
			+ " AND ((?2 BETWEEN rh.data_entrada AND rh.data_saida) OR (?3 BETWEEN rh.data_entrada AND rh.data_saida))", nativeQuery = true)
	Boolean isHospedado(String documento, Date dataEntrada, Date dataSaida);

}
