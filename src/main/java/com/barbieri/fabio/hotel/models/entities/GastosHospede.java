package com.barbieri.fabio.hotel.models.entities;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Immutable
@Table(name = "gastos_hospedes")
public class GastosHospede {

	@Id
	@JsonIgnore
	private String documento;
	private double valorUltimaHospedagem;
	private double valorTotalHospedagens;

	public GastosHospede() {
	}

	public GastosHospede(String documento, double valorUltimaHospedagem, double valorTotalHospedagens) {
		super();
		this.documento = documento;
		this.valorUltimaHospedagem = valorUltimaHospedagem;
		this.valorTotalHospedagens = valorTotalHospedagens;
	}

	public String getDocumento() {
		return documento;
	}

	public double getValorTotalHospedagens() {
		return valorTotalHospedagens;
	}

	public double getValorUltimaHospedagem() {
		return valorUltimaHospedagem;
	}

	public void setDocumento(String hospede) {
		this.documento = hospede;
	}

	public void setValorTotalHospedagens(double valorTotalHospedagens) {
		this.valorTotalHospedagens = valorTotalHospedagens;
	}

	public void setValorUltimaHospedagem(double valorUltimaHospedagem) {
		this.valorUltimaHospedagem = valorUltimaHospedagem;
	}

	@Override
	public int hashCode() {
		return Objects.hash(documento, valorTotalHospedagens, valorUltimaHospedagem);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GastosHospede other = (GastosHospede) obj;
		return Objects.equals(documento, other.documento)
				&& Double.doubleToLongBits(valorTotalHospedagens) == Double
						.doubleToLongBits(other.valorTotalHospedagens)
				&& Double.doubleToLongBits(valorUltimaHospedagem) == Double
						.doubleToLongBits(other.valorUltimaHospedagem);
	}

}
