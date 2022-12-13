package com.barbieri.fabio.hotel.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.barbieri.fabio.hotel.helpers.DebugHelper;
import com.barbieri.fabio.hotel.models.entities.Hospede;
import com.barbieri.fabio.hotel.models.entities.RegistroHospedagem;
import com.barbieri.fabio.hotel.models.repositories.HospedeRepository;
import com.barbieri.fabio.hotel.models.repositories.RegistroHospedagemRepository;

@RestController
public class RegistroHospedagemController {

	@Autowired
	RegistroHospedagemRepository hospedagemRepository;

	@Autowired
	HospedeRepository hospedeRepository;
	
	DebugHelper debug = new DebugHelper();

	@PostMapping("/api/check-in")
	public ResponseEntity<RegistroHospedagem> checkIn(@RequestBody @Valid RegistroHospedagem registroHospedagem) {
		
		if(registroHospedagem.getDataSaida().getTime() <= registroHospedagem.getDataEntrada().getTime() ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Período de hospedagem inválido!");
		}
		
		Hospede hospede = registroHospedagem.getHospede();

		Iterable<Hospede> findHospede = hospedeRepository.findByDocumentoOrNomeOrTelefone(hospede.getDocumento(),
				hospede.getNome(), hospede.getTelefone());

		if (!findHospede.iterator().hasNext()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hóspede não encontrado!");
		}

		registroHospedagem.setHospede(findHospede.iterator().next());

		if (hospedagemRepository.isHospedado(registroHospedagem.getHospede().getDocumento(),
				registroHospedagem.getDataEntrada(), registroHospedagem.getDataSaida())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Hóspede já possui hospedagem agendada nesta data!");
		}

		registroHospedagem.calculaDiaria();
		registroHospedagem = hospedagemRepository.save(registroHospedagem);

		return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
				.body(registroHospedagem);
	}

	@GetMapping("api/hospedagens")
	public @ResponseBody Iterable<RegistroHospedagem> getAll() {
		return hospedagemRepository.findAll();
	}
}
