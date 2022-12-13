package com.barbieri.fabio.hotel.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.barbieri.fabio.hotel.models.entities.Hospede;
import com.barbieri.fabio.hotel.models.repositories.HospedeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(HospedeController.class)
public class HospedeControllerTests {

	@MockBean
	HospedeRepository repository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	LinkedList<Hospede> getAllHospedes() {
		return new LinkedList<>(Arrays.asList(new Hospede("José da Silva", "123.456.789-00", "1234-5678"),
				new Hospede("Santiago Pereira", "111.222.333-00", "1111-1111"),
				new Hospede("Maria dos Santos", "987.654.321-00", "8765-4321")));
	}

	@Test
	@DisplayName("GET /api/hospedes - findAll")
	void shouldFindAllHospedes() throws Exception {
		List<Hospede> exemploHospedes = getAllHospedes();

		when(repository.findAll()).thenReturn(exemploHospedes);

		MvcResult result = mockMvc.perform(get("/api/hospedes"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		Iterable<Hospede> resultadoHospedes = new LinkedList<>(Arrays.asList(objectMapper
				.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Hospede[].class)));

		verify(repository).findAll();

		assertIterableEquals(exemploHospedes, resultadoHospedes);
	}

	@Test
	@DisplayName("GET /api/hospedes - Find by documento")
	void shouldFindHospedesByDocumentoContaining() throws Exception {
		String documento = ".3";

		LinkedList<Hospede> exemploHospedes = new LinkedList<>(getAllHospedes().stream()
				.filter(hospede -> hospede.getDocumento().contains(documento)).collect(Collectors.toList()));

		when(repository.findByDocumentoContaining(documento)).thenReturn(exemploHospedes);

		MvcResult result = mockMvc.perform(get("/api/hospedes").param("documento", documento))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		Iterable<Hospede> resultadoHospedes = new LinkedList<>(Arrays.asList(objectMapper
				.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Hospede[].class)));

		assertIterableEquals(
				new LinkedList<>(Arrays.asList(new Hospede("Santiago Pereira", "111.222.333-00", "1111-1111"),
						new Hospede("Maria dos Santos", "987.654.321-00", "8765-4321"))),
				resultadoHospedes);
	}

	@Test
	@DisplayName("GET /api/hospedes - Not find by documento")
	void shouldNotFindHospedesByDocumentoContaining() throws Exception {
		String documento = "999.999.999-99";

		LinkedList<Hospede> exemploHospedes = new LinkedList<>(getAllHospedes().stream()
				.filter(hospede -> hospede.getDocumento().contains(documento)).collect(Collectors.toList()));

		when(repository.findByDocumentoContaining(documento)).thenReturn(exemploHospedes);

		MvcResult result = mockMvc.perform(get("/api/hospedes").param("documento", documento))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		Iterable<Hospede> resultadoHospedes = new LinkedList<>(Arrays.asList(objectMapper
				.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Hospede[].class)));

		assertIterableEquals(new LinkedList<>(), resultadoHospedes);
	}

	@Test
	@DisplayName("GET /api/hospedes - Find by nome and telefone")
	void shouldFindByNomeContainingIgnoreCaseAndTelefoneContaining() throws Exception {
		String nome = "José";
		String telefone = "1234";

		LinkedList<Hospede> exemploHospedes = new LinkedList<>(
				getAllHospedes().stream().filter(hospede -> hospede.getNome().toLowerCase().contains(nome.toLowerCase())
						&& hospede.getTelefone().contains(telefone)).collect(Collectors.toList()));

		when(repository.findByNomeContainingIgnoreCaseAndTelefoneContaining(nome, telefone))
				.thenReturn(exemploHospedes);

		MvcResult result = mockMvc.perform(get("/api/hospedes").param("nome", nome).param("telefone", telefone))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		Iterable<Hospede> resultadoHospedes = new LinkedList<>(Arrays.asList(objectMapper
				.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Hospede[].class)));

		assertIterableEquals(
				new LinkedList<>(Arrays.asList(new Hospede("José da Silva", "123.456.789-00", "1234-5678"))),
				resultadoHospedes);
	}

	@Test
	@DisplayName("GET /api/hospedes - Not find by nome and telefone")
	void shouldNotFindByNomeContainingIgnoreCaseAndTelefoneContaining() throws Exception {
		String nome = "Pedro";
		String telefone = "1234";

		LinkedList<Hospede> exemploHospedes = new LinkedList<>(getAllHospedes().stream().filter(
				hospede -> hospede.getNome().toLowerCase().contains(nome) && hospede.getTelefone().contains(telefone))
				.collect(Collectors.toList()));

		when(repository.findByNomeContainingIgnoreCaseAndTelefoneContaining(nome, telefone))
				.thenReturn(exemploHospedes);

		MvcResult result = mockMvc.perform(get("/api/hospedes").param("nome", nome).param("telefone", telefone))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		Iterable<Hospede> resultadoHospedes = new LinkedList<>(Arrays.asList(objectMapper
				.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Hospede[].class)));

		assertIterableEquals(new LinkedList<>(), resultadoHospedes);
	}

	@Test
	@DisplayName("GET /api/hospedes - Find by nome")
	void shouldFindByNomeContaining() throws Exception {
		String nome = "José";

		LinkedList<Hospede> exemploHospedes = new LinkedList<>(getAllHospedes().stream()
				.filter(hospede -> hospede.getNome().toLowerCase().contains(nome.toLowerCase()))
				.collect(Collectors.toList()));

		when(repository.findByNomeContainingIgnoreCase(nome)).thenReturn(exemploHospedes);

		MvcResult result = mockMvc.perform(get("/api/hospedes").param("nome", nome))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		Iterable<Hospede> resultadoHospedes = new LinkedList<>(Arrays.asList(objectMapper
				.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Hospede[].class)));

		assertIterableEquals(
				new LinkedList<>(Arrays.asList(new Hospede("José da Silva", "123.456.789-00", "1234-5678"))),
				resultadoHospedes);
	}

	@Test
	@DisplayName("GET /api/hospedes - Not find by nome")
	void shouldNotFindByNomeContaining() throws Exception {
		String nome = "Pedro";

		LinkedList<Hospede> exemploHospedes = new LinkedList<>(getAllHospedes().stream()
				.filter(hospede -> hospede.getNome().toLowerCase().contains(nome.toLowerCase()))
				.collect(Collectors.toList()));

		when(repository.findByNomeContainingIgnoreCase(nome)).thenReturn(exemploHospedes);

		MvcResult result = mockMvc.perform(get("/api/hospedes").param("nome", nome))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		Iterable<Hospede> resultadoHospedes = new LinkedList<>(Arrays.asList(objectMapper
				.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Hospede[].class)));

		assertIterableEquals(new LinkedList<>(), resultadoHospedes);
	}

	@Test
	@DisplayName("GET /api/hospedes - Find by telefone")
	void shouldFindByTelefoneContaining() throws Exception {
		String telefone = "1234";

		LinkedList<Hospede> exemploHospedes = new LinkedList<>(getAllHospedes().stream()
				.filter(hospede -> hospede.getTelefone().contains(telefone)).collect(Collectors.toList()));

		when(repository.findByTelefoneContaining(telefone)).thenReturn(exemploHospedes);

		MvcResult result = mockMvc.perform(get("/api/hospedes").param("telefone", telefone))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		Iterable<Hospede> resultadoHospedes = new LinkedList<>(Arrays.asList(objectMapper
				.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Hospede[].class)));

		assertIterableEquals(
				new LinkedList<>(Arrays.asList(new Hospede("José da Silva", "123.456.789-00", "1234-5678"))),
				resultadoHospedes);
	}

	@Test
	@DisplayName("GET /api/hospedes - Not find by telefone")
	void shouldNotFindByTelefoneContaining() throws Exception {
		String telefone = "9999-9999";

		LinkedList<Hospede> exemploHospedes = new LinkedList<>(getAllHospedes().stream()
				.filter(hospede -> hospede.getTelefone().contains(telefone)).collect(Collectors.toList()));

		when(repository.findByTelefoneContaining(telefone)).thenReturn(exemploHospedes);

		MvcResult result = mockMvc.perform(get("/api/hospedes").param("telefone", telefone))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		Iterable<Hospede> resultadoHospedes = new LinkedList<>(Arrays.asList(objectMapper
				.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Hospede[].class)));

		assertIterableEquals(new LinkedList<>(), resultadoHospedes);
	}

	@Test
	@DisplayName("POST /api/hospedes - Created")
	void shouldCreateHospede() throws Exception {
		Hospede hospede = new Hospede("José da Silva", "123.456.789-00", "1234-5678");

		mockMvc.perform(post("/api/hospedes").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(hospede))).andExpect(status().isCreated());
	}

	@Test
	@DisplayName("POST /api/hospedes - Hospede já cadastrado")
	void shouldNotUpdateHospede() throws Exception {
		Hospede hospede = new Hospede("José da Silva", "123.456.789-00", "1111-1111");

		doReturn(true).when(repository).existsById(hospede.getDocumento());

		mockMvc.perform(post("/api/hospedes").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(hospede))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PUT /api/hospedes - OK")
	void shouldUpdateHospede() throws Exception {
		Hospede hospede = new Hospede("José da Silva", "123.456.789-00", "1111-1111");

		doReturn(true).when(repository).existsById(hospede.getDocumento());
		doReturn(hospede).when(repository).save(hospede);

		hospede.setTelefone("2222-2222");

		MvcResult result = mockMvc.perform(put("/api/hospedes").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(hospede))).andExpect(status().isOk()).andReturn();

		Hospede retorno = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
				Hospede.class);

		assertEquals("2222-2222", retorno.getTelefone());
	}

	@Test
	@DisplayName("PUT /api/hospedes - Hospede não encontrado")
	void shouldNotCreateHospede() throws Exception {
		Hospede hospede = new Hospede("José da Silva", "123.456.789-00", "1111-1111");

		doReturn(false).when(repository).existsById(hospede.getDocumento());

		mockMvc.perform(put("/api/hospedes").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(hospede))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("DELETE /api/hospedes - OK")
	void shouldDeleteHospede() throws Exception {
		String documento = "123.456.789-00";

		mockMvc.perform(delete("/api/hospedes").queryParam("documento", documento)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("DELETE /api/hospedes - Documento is Null")
	void shouldNotDeleteHospede() throws Exception {
		String documento = null;

		mockMvc.perform(delete("/api/hospedes").queryParam("documento", documento)).andExpect(status().isBadRequest());
	}

}
