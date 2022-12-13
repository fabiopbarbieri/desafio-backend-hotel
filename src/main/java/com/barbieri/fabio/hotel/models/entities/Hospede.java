package com.barbieri.fabio.hotel.models.entities;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "hospedes")
public class Hospede {

	@NotBlank
	private String nome;

	@Id
	@NotBlank
	@Length(min = 14, max = 14)
	private String documento;

	@NotBlank
	@Length(min = 8, max = 20)
	private String telefone;

	@OneToOne
	@JoinColumn(name = "documento")
	private GastosHospede gastos;

	public Hospede() {
	}

	public Hospede(@NotBlank String nome, @NotBlank String documento, @NotBlank String telefone) {
		this.nome = nome;
		this.documento = documento;
		this.telefone = telefone;
	}

	public String getDocumento() {
		return documento;
	}

	public GastosHospede getGastos() {
		return gastos;
	}

	public String getNome() {
		return nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public void setGastos(GastosHospede gastos) {
		this.gastos = gastos;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	@Override
	public int hashCode() {
		return Objects.hash(documento, nome, telefone);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hospede other = (Hospede) obj;
		return Objects.equals(documento, other.documento) && Objects.equals(nome, other.nome)
				&& Objects.equals(telefone, other.telefone);
	}

}
