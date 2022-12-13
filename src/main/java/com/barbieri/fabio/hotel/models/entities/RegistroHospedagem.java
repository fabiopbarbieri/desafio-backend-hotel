package com.barbieri.fabio.hotel.models.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.barbieri.fabio.hotel.helpers.DateHelper;
import com.barbieri.fabio.hotel.models.ValoresDiaria;

@Entity
public class RegistroHospedagem {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "documento_hospede")
	private Hospede hospede;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private Date dataEntrada;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	Date dataSaida;

	private boolean adicionalVeiculo;

	private double valorHospedagem;

	public RegistroHospedagem() {
	}

	public RegistroHospedagem(@NotBlank Hospede hospede, @NotBlank Date dataEntrada, @NotBlank Date dataSaida) {
		this(hospede, dataEntrada, dataSaida, false);
	}

	public RegistroHospedagem(@NotBlank Hospede hospede, @NotBlank Date dataEntrada, @NotBlank Date dataSaida,
			boolean adicionalVeiculo) {
		this.hospede = hospede;
		this.dataEntrada = dataEntrada;
		this.dataSaida = dataSaida;
		this.adicionalVeiculo = adicionalVeiculo;
	}

	public void calculaDiaria() {
		DateHelper dateHelper = new DateHelper();
		Date dataSaida = getDataSaida();

		if (dateHelper.isAfterHorarioSaida(dataSaida)) {
			// Caso o horario de saída seja após o horario definido em ValoresDiaria.class
			// adiciona 1 diária extra (será cobrado o dia seguinte)
			dataSaida = dateHelper.plusDays(dataSaida, 1);
		}

		this.valorHospedagem = 0;

		for (Date data = getDataEntrada(); data.getTime() <= dataSaida.getTime(); data = dateHelper.plusDays(data, 1)) {
			this.valorHospedagem += dateHelper.isWeekday(data) ? ValoresDiaria.DIARIA_DURANTE_SEMANA
					: ValoresDiaria.DIARIA_FIM_SEMANA;

			if (isAdicionalVeiculo()) {
				this.valorHospedagem += dateHelper.isWeekday(data) ? ValoresDiaria.DIARIA_GARAGEM_DURANTE_SEMANA
						: ValoresDiaria.DIARIA_GARAGEM_FIM_SEMANA;
			}
		}

	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public Date getDataSaida() {
		return dataSaida;
	}

	public Hospede getHospede() {
		return hospede;
	}

	public int getId() {
		return id;
	}

	public double getValorHospedagem() {
		return valorHospedagem;
	}

	public boolean isAdicionalVeiculo() {
		return adicionalVeiculo;
	}

	public void setAdicionalVeiculo(boolean adicionalVeiculo) {
		this.adicionalVeiculo = adicionalVeiculo;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	public void setHospede(Hospede hospede) {
		this.hospede = hospede;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(adicionalVeiculo, dataEntrada, dataSaida, hospede, id, valorHospedagem);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegistroHospedagem other = (RegistroHospedagem) obj;
		return adicionalVeiculo == other.adicionalVeiculo && Objects.equals(dataEntrada, other.dataEntrada)
				&& Objects.equals(dataSaida, other.dataSaida) && Objects.equals(hospede, other.hospede)
				&& id == other.id
				&& Double.doubleToLongBits(valorHospedagem) == Double.doubleToLongBits(other.valorHospedagem);
	}

}
