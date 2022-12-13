package com.barbieri.fabio.hotel.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.barbieri.fabio.hotel.models.entities.GastosHospede;
import com.barbieri.fabio.hotel.models.entities.Hospede;
import com.barbieri.fabio.hotel.models.entities.RegistroHospedagem;
import com.barbieri.fabio.hotel.models.repositories.HospedeRepository;
import com.barbieri.fabio.hotel.models.repositories.RegistroHospedagemRepository;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.NONE)	
public class RepositoriesIntegrationTests {

	@Autowired
	private HospedeRepository hospedeRepository;

	@Autowired
	private RegistroHospedagemRepository hospedagemRepository;

	@Container
	public static PostgreSQLContainer container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
			.withDatabaseName("hotel");

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues
					.of("spring.datasource.url=" + container.getJdbcUrl(),
							"spring.datasource.username=" + container.getUsername(),
							"spring.datasource.password=" + container.getPassword())
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}

	@BeforeAll
	public static void setUp() {
		container.withReuse(true);
		container.withInitScript("src/main/resources/schema.sql");
		container.start();
	}

	@DynamicPropertySource
	public static void overrideProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", container::getJdbcUrl);
		registry.add("spring.datasource.username", container::getUsername);
		registry.add("spring.datasource.password", container::getPassword);
		registry.add("spring.datasource.driver-class-name", container::getDriverClassName);
	}

	@AfterAll
	public static void tearDown() {
		container.stop();
	}

	@BeforeEach
	private void setUpEach() {
		hospedagemRepository.deleteAll();
		hospedeRepository.deleteAll();
	}

	@Test
	@DisplayName("GET /api/hospedes/hospedados")
	void shouldFindHospedesHospedados() {
		// Setup inicial dos hóspedes
		Hospede jose = new Hospede("José da Silva", "123.456.789-00", "1234-5678");
		Hospede maria = new Hospede("Maria Pereira", "111.111.789-00", "8765-4321");
		Hospede carlos = new Hospede("Carlos Souza", "222.222.222-00", "2222-2222");

		hospedeRepository.saveAll(new ArrayList<>(Arrays.asList(maria, jose, carlos)));

		// Realiza o check-in do hóspede que deverá constar como 'hospedado'
		RegistroHospedagem registroHospedagem = new RegistroHospedagem(maria,
				new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)),
				new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));

		registroHospedagem.calculaDiaria();

		maria.setGastos(new GastosHospede(maria.getDocumento(), registroHospedagem.getValorHospedagem(),
				registroHospedagem.getValorHospedagem()));

		hospedagemRepository.save(registroHospedagem);

		// Realiza o check-in do mesmo hóspede, no passado
		registroHospedagem = new RegistroHospedagem(maria,
				new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(20)),
				new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(15)));

		registroHospedagem.calculaDiaria();

		hospedagemRepository.save(registroHospedagem);

		maria.getGastos().setValorTotalHospedagens(
				maria.getGastos().getValorTotalHospedagens() + registroHospedagem.getValorHospedagem());

		// Realiza o check-in de um hóspede que não está hospedado no hotel ainda
		registroHospedagem = new RegistroHospedagem(jose,
				new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3)),
				new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5)));

		registroHospedagem.calculaDiaria();

		hospedagemRepository.save(registroHospedagem);

		jose.setGastos(new GastosHospede(jose.getDocumento(), registroHospedagem.getValorHospedagem(),
				registroHospedagem.getValorHospedagem()));

		// Testa a consulta que é realizada no endpoint /api/hospedes/hospedados
		Iterable<Hospede> hospedados = hospedeRepository.findHospedesHospedados();

		assertIterableEquals(new ArrayList<>(Arrays.asList(maria)), hospedados);
		assertEquals(maria.getGastos(), hospedados.iterator().next().getGastos());
	}

	@Test
	@DisplayName("GET /api/hospedes/nao-hospedados")
	void shouldFindHospedesNaoHospedados() {
		// Setup inicial dos hóspedes
		Hospede jose = new Hospede("José da Silva", "123.456.789-00", "1234-5678");
		Hospede maria = new Hospede("Maria Pereira", "111.111.789-00", "8765-4321");
		Hospede carlos = new Hospede("Carlos Souza", "222.222.222-00", "2222-2222");

		hospedeRepository.saveAll(new ArrayList<>(Arrays.asList(maria, jose, carlos)));

		// Realiza o check-in do hóspede que deverá constar como 'hospedado'
		RegistroHospedagem registroHospedagem = new RegistroHospedagem(maria,
				new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)),
				new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));

		registroHospedagem.calculaDiaria();

		maria.setGastos(new GastosHospede(maria.getDocumento(), registroHospedagem.getValorHospedagem(),
				registroHospedagem.getValorHospedagem()));

		hospedagemRepository.save(registroHospedagem);

		// Realiza o check-in de um hóspede que não está mais hospedado no hotel
		registroHospedagem = new RegistroHospedagem(jose,
				new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(5)),
				new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)));

		registroHospedagem.calculaDiaria();

		jose.setGastos(new GastosHospede(jose.getDocumento(), registroHospedagem.getValorHospedagem(),
				registroHospedagem.getValorHospedagem()));

		hospedagemRepository.save(registroHospedagem);

		// Testa a consulta que é realizada no endpoint /api/hospedes/nao-hospedados
		Iterable<Hospede> naoHospedados = hospedeRepository.findHospedesNaoHospedados();

		assertIterableEquals(new ArrayList<>(Arrays.asList(jose)), naoHospedados);

		// System.out.println("\n esperado: " + new
		// DebugHelper().toString(jose.getGastos()));
		// System.out.println("\n veio: " + new
		// DebugHelper().toString(naoHospedados.iterator().next().getGastos()));

		assertEquals(jose.getGastos(), naoHospedados.iterator().next().getGastos());
	}
}
