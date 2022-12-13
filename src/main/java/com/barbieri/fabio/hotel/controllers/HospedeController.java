package com.barbieri.fabio.hotel.controllers;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.barbieri.fabio.hotel.models.entities.Hospede;
import com.barbieri.fabio.hotel.models.repositories.HospedeRepository;

@RestController
@RequestMapping("/api/hospedes")
public class HospedeController {

	@Autowired
	private HospedeRepository repository;

	@GetMapping("/hospedados")
	public @ResponseBody Iterable<Hospede> getHospedesHospedados() {
		return repository.findHospedesHospedados();
	}

	@GetMapping("/nao-hospedados")
	public @ResponseBody Iterable<Hospede> getHospedesNaoHospedados() {
		return repository.findHospedesNaoHospedados();
	}

	@GetMapping
	public @ResponseBody Iterable<Hospede> getHospede(
			@RequestParam(name = "documento", required = false) String documento,
			@RequestParam(name = "nome", required = false) String nome,
			@RequestParam(name = "telefone", required = false) String telefone) {

		if (documento != null && !documento.isEmpty()) {
			return repository.findByDocumentoContaining(documento);
		}

		if (nome != null && !nome.isEmpty() && telefone != null && !telefone.isEmpty()) {
			return repository.findByNomeContainingIgnoreCaseAndTelefoneContaining(nome, telefone);
		}

		if (nome != null && !nome.isEmpty()) {
			return repository.findByNomeContainingIgnoreCase(nome);
		}

		if (telefone != null && !telefone.isEmpty()) {
			return repository.findByTelefoneContaining(telefone);
		}

		return repository.findAll();
	}

	@PostMapping
	public @ResponseBody ResponseEntity<Hospede> addHospede(@RequestBody @Valid Hospede hospede) {
		if (repository.existsById(hospede.getDocumento())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este hóspede já está cadastrado");
		}

		return new ResponseEntity<>(repository.save(hospede), HttpStatus.CREATED);
	}

	@PutMapping
	public @ResponseBody Hospede updateHospede(@RequestBody @Valid Hospede hospede) {
		if (!repository.existsById(hospede.getDocumento())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hóspede não encontrado");
		}

		return repository.save(hospede);
	}

	@DeleteMapping
	public @ResponseBody void removeHospede(
			@Valid @NotBlank @RequestParam(name = "documento", required = true) String documento) {
		repository.deleteById(documento);
	}

}
