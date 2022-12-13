package com.barbieri.fabio.hotel.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.barbieri.fabio.hotel.models.entities.Hospede;
import com.barbieri.fabio.hotel.models.entities.RegistroHospedagem;
import com.barbieri.fabio.hotel.models.repositories.HospedeRepository;
import com.barbieri.fabio.hotel.models.repositories.RegistroHospedagemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(RegistroHospedagemController.class)
public class RegistroHospedagemControllerTests {

	@MockBean
	HospedeRepository hospedeRepository;

	@MockBean
	RegistroHospedagemRepository hospedagemRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("GET /api/hospedagens - findAll")
	void shouldFindAllHospedes() throws Exception {
		Hospede jose = new Hospede("José da Silva", "123.456.789-00", "1234-5678");
		Hospede santiago = new Hospede("Santiago Pereira", "111.222.333-00", "1111-1111");

		List<RegistroHospedagem> exemploHospedagens = new ArrayList<>(Arrays.asList(
				new RegistroHospedagem(santiago, new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)),
						new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))),
				new RegistroHospedagem(jose, new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)),
						new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))),
				new RegistroHospedagem(santiago, new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10)),
						new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(5)))));

		exemploHospedagens.forEach(RegistroHospedagem::calculaDiaria);

		when(hospedagemRepository.findAll()).thenReturn(exemploHospedagens);

		MvcResult result = mockMvc.perform(get("/api/hospedagens"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		Iterable<RegistroHospedagem> resultadoHospedagens = new LinkedList<>(Arrays.asList(objectMapper.readValue(
				result.getResponse().getContentAsString(StandardCharsets.UTF_8), RegistroHospedagem[].class)));

		verify(hospedagemRepository).findAll();

		assertIterableEquals(exemploHospedagens, resultadoHospedagens);
	}

	@Test
	@DisplayName("POST /api/check-in - Created")
	void shouldCheckInHospede() throws Exception {
		Hospede hospede = new Hospede("José da Silva", "123.456.789-00", "1111-1111");

		RegistroHospedagem hospedagem = new RegistroHospedagem(hospede,
				new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)),
				new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));

		hospedagem.calculaDiaria();

		doReturn(new ArrayList<>(Arrays.asList(hospede))).when(hospedeRepository)
				.findByDocumentoOrNomeOrTelefone(hospede.getDocumento(), hospede.getNome(), hospede.getTelefone());

		doReturn(false).when(hospedagemRepository).isHospedado(hospede.getDocumento(), hospedagem.getDataEntrada(),
				hospedagem.getDataSaida());

		doReturn(hospedagem).when(hospedagemRepository).save(hospedagem);

		MvcResult result = mockMvc
				.perform(post("/api/check-in").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(hospedagem)))
				.andExpect(status().isCreated()).andReturn();

		RegistroHospedagem resultado = objectMapper
				.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), RegistroHospedagem.class);

		assertEquals(hospedagem, resultado);

	}

	@Test
	@DisplayName("POST /api/check-in - Hóspede já possui hospedagem agendada nesta data!")
	void shouldNotCheckInHospedeDupeDate() throws Exception {
		Hospede hospede = new Hospede("José da Silva", "123.456.789-00", "1111-1111");

		RegistroHospedagem hospedagem = new RegistroHospedagem(hospede,
				new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)),
				new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));

		hospedagem.calculaDiaria();

		doReturn(new ArrayList<>(Arrays.asList(hospede))).when(hospedeRepository)
				.findByDocumentoOrNomeOrTelefone(hospede.getDocumento(), hospede.getNome(), hospede.getTelefone());

		doReturn(true).when(hospedagemRepository).isHospedado(hospede.getDocumento(), hospedagem.getDataEntrada(),
				hospedagem.getDataSaida());

		mockMvc.perform(post("/api/check-in").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(hospedagem))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/hospedes - Hóspede não encontrado!")
	void shouldNotCheckInHospedeNotFind() throws Exception {
		Hospede hospede = new Hospede("José da Silva", "123.456.789-00", "1111-1111");

		RegistroHospedagem hospedagem = new RegistroHospedagem(hospede,
				new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)),
				new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));

		hospedagem.calculaDiaria();

		doReturn(new ArrayList<>()).when(hospedeRepository)
				.findByDocumentoOrNomeOrTelefone(hospede.getDocumento(), hospede.getNome(), hospede.getTelefone());

		mockMvc.perform(post("/api/check-in").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(hospedagem))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/check-in - Período de hospedagem inválido!")
	void shouldNotCheckInHospedeInvalidDates() throws Exception {
		Hospede hospede = new Hospede("José da Silva", "123.456.789-00", "1111-1111");

		RegistroHospedagem hospedagem = new RegistroHospedagem(hospede,
				new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)),
				new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));

		mockMvc.perform(post("/api/check-in").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(hospedagem))).andExpect(status().isBadRequest());
	}

}
