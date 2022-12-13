package com.barbieri.fabio.hotel.helpers;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DebugHelper {

	private final ObjectMapper JACKSON = new ObjectMapper();

	public DebugHelper() {
		JACKSON.setDateFormat(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));
	}

	public final String toString(Object objeto) {
		try {
			return JACKSON.writeValueAsString(objeto);
		} catch (Exception e) {
			return null;
		}
	}
}
