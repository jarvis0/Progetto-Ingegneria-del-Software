package it.polimi.ingsw.ps23.server.model.player;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.ps23.server.model.InvalidPositionException;
import it.polimi.ingsw.ps23.server.model.map.regions.City;
import it.polimi.ingsw.ps23.server.model.map.regions.NormalCity;

public class BuiltEmporiumsSet {
	
	private static final int MAX_EMPORIUMS = 2;
	
	private List<City> builtEmporiums;
	
	public BuiltEmporiumsSet() {
		builtEmporiums = new ArrayList<>();
	}
	
	public void addBuiltEmporium(City city) throws InvalidPositionException {
		if(!builtEmporiums.contains(city)) {
			builtEmporiums.add(city);
		}
		else {
			throw new InvalidPositionException();
		}
	}
	
	public boolean containsMaxEmporium() { //TODO max emporium
		return builtEmporiums.size() == MAX_EMPORIUMS;
	}

	public List<City> getBuiltEmporiumSet() {
		return builtEmporiums;
	}

	public BuiltEmporiumsSet getCitiesForRecycleRewardTokens() {
		BuiltEmporiumsSet citiesWithoutNobilityTrackPoints = new BuiltEmporiumsSet();
		for(City city : builtEmporiums) {
			if(city instanceof NormalCity && !((NormalCity)city).hasNobilityTrackBonus()) {
				citiesWithoutNobilityTrackPoints.builtEmporiums.add(city);
			}
		}
		return citiesWithoutNobilityTrackPoints;				
	}

	public NormalCity getChosenCity(String cityName) { //TODO sistemare la return null
		for(City city : builtEmporiums) {
			if(city.getName().equals(cityName)) {
				return (NormalCity) city;
			}	
		}
		return null;
	}

	public String getCities() {
		StringBuilder loopString = new StringBuilder();
		City city;
		if(!builtEmporiums.isEmpty()) {
			city = builtEmporiums.get(0);
			loopString.append(city.getName() + " " + city.getColor());
		}
		for(int i = 1; i < builtEmporiums.size(); i++) {
			city = builtEmporiums.get(i);
			loopString.append(", " + city.getName() + " " + city.getColor());
		}
		return "[" + new String() + loopString + "]";
	}

	@Override
	public String toString() {
		return builtEmporiums.toString();
	}

}
