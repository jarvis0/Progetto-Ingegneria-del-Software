package it.polimi.ingsw.ps23.server.commons.exceptions;

import java.io.IOException;
/**
 * IOexception that notify the players that the current {@link Player} have selected an invalid councillor.
 * @author Mirco Manzoni
 *
 */
public class InvalidCouncillorException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4109956462010705543L;
	private static final String EXCEPTION_STRING = "The current player has selected an illegal councillor from the pool.";
	
	@Override
	public String toString() {
		return EXCEPTION_STRING;
	}
}
