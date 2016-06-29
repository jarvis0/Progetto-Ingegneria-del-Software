package it.polimi.ingsw.ps23.server.commons.exceptions;

import java.io.IOException;

public class InvalidCityException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7745659014979461534L;
	private static final String EXCEPTION_STRING = "You have selected an illegal city.";
	
	@Override
	public String toString() {
		return EXCEPTION_STRING;
	}


}