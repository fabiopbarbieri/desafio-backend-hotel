package com.barbieri.fabio.hotel.helpers;

import java.util.Calendar;
import java.util.Date;

import com.barbieri.fabio.hotel.models.ValoresDiaria;

public class DateHelper {

	public int getWeekDay(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public boolean isAfterHorarioSaida(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);

		return cal.get(Calendar.HOUR_OF_DAY) >= ValoresDiaria.HORA_SAIDA_DIARIA_EXTRA
				&& cal.get(Calendar.MINUTE) >= ValoresDiaria.MINUTO_SAIDA_DIARIA_EXTRA;
	}

	public boolean isWeekday(Date data) {
		return isWeekday(getWeekDay(data));
	}

	public boolean isWeekday(int diaDaSemana) {
		return !isWeekend(diaDaSemana);
	}

	public boolean isWeekend(Date data) {
		return isWeekend(getWeekDay(data));
	}

	public boolean isWeekend(int diaDaSemana) {
		return diaDaSemana == Calendar.SATURDAY || diaDaSemana == Calendar.SUNDAY;
	}

	public Date plusDays(Date data, int dias) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.add(Calendar.DAY_OF_MONTH, dias);
		return cal.getTime();
	}
}
