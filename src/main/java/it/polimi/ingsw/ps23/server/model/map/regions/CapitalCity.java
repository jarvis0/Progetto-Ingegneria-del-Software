package it.polimi.ingsw.ps23.server.model.map.regions;

import it.polimi.ingsw.ps23.server.model.map.GameColor;

public class CapitalCity extends City {
	
	public CapitalCity(String name, GameColor color){
		super(name, color);
	}

	@Override
	public String toString() {
		return getName() + " " + getColor() + " " + getEmporiumsPlayersList();
	}
	
}