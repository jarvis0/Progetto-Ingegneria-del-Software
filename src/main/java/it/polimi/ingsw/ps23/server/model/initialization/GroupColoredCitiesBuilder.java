package it.polimi.ingsw.ps23.server.model.initialization;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.ps23.server.model.bonus.Bonus;
import it.polimi.ingsw.ps23.server.model.bonus.RealBonus;
import it.polimi.ingsw.ps23.server.model.bonus.VictoryPointBonus;
import it.polimi.ingsw.ps23.server.model.map.Region;
import it.polimi.ingsw.ps23.server.model.map.board.GroupColoredCity;
import it.polimi.ingsw.ps23.server.model.map.regions.City;

class GroupColoredCitiesBuilder {
	
	private static final int COLOR_POSITION = 0;
	private static final int BONUS_VALUE_POSITION = 1;
	private static final int BONUS_NAME_POSITION = 0;

	private List<Region> coloredGroupCities;
	
	GroupColoredCitiesBuilder() {
		coloredGroupCities = new ArrayList<>();
	}
	
	private void addCities(List<City> cities, String colorName, Region coloredGroup) {
		for(City city : cities) {
			if(city.getColor().equals(colorName)) {
				coloredGroup.addCity(city);
			}
		}
	}
	
	List<Region> makeGroup(List<String[]> rawColoredCities, List<City> cities) {
		String[] fields = rawColoredCities.remove(rawColoredCities.size() - 1);
		for(String[] rawColoredCity : rawColoredCities) {
			String colorName = rawColoredCity[COLOR_POSITION];
			Bonus bonus = new VictoryPointBonus(fields[BONUS_NAME_POSITION]);
			((RealBonus)bonus).setValue(Integer.parseInt(rawColoredCity[BONUS_VALUE_POSITION]));
			Region coloredGroup = new GroupColoredCity(colorName, bonus);
			addCities(cities, colorName, coloredGroup);
			coloredGroupCities.add(coloredGroup);
			coloredGroup.toCitiesList();
		}
		return coloredGroupCities;
	}
	
}
