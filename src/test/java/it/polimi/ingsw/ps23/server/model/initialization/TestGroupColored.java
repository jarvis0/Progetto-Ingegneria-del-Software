package it.polimi.ingsw.ps23.server.model.initialization;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import it.polimi.ingsw.ps23.server.model.bonus.BonusCache;
import it.polimi.ingsw.ps23.server.model.bonus.RealBonus;
import it.polimi.ingsw.ps23.server.model.map.Region;
/**
 * Tests the construction of the {@link GroupColoredCity} retrieving data from configuration files.
 * @author Mirco Manzoni
 *
 */
public class TestGroupColored {

	private static final String TEST_CONFIGURATION_PATH = "src/test/java/it/polimi/ingsw/ps23/server/model/initialization/configuration/";
	private static final String CITIES_CSV = "cities.csv";
	private static final String REWARD_TOKENS_CSV = "rewardTokens.csv";
	private static final String GROUP_COLORED_CSV = "groupColoredCitiesBonusTiles.csv";
	
	@Test
	public void test() {
		List<String[]> rawCities = new RawObject(TEST_CONFIGURATION_PATH + CITIES_CSV).getRawObject();
		List<String[]> rawRewardTokens = new RawObject(TEST_CONFIGURATION_PATH + REWARD_TOKENS_CSV).getRawObject();
		CitiesBuilder citiesFactory = new CitiesBuilder();
		citiesFactory.makeCities(rawCities, rawRewardTokens, new BonusCache());
		List<String[]> rawColoredCities = new RawObject(TEST_CONFIGURATION_PATH + GROUP_COLORED_CSV).getRawObject();
		List<Region> groupColored = new GroupColoredCitiesBuilder().makeGroup(rawColoredCities, citiesFactory.getCities());
		assertTrue(groupColored.get(0).getName().equals("iron"));
		assertTrue(groupColored.get(1).getName().equals("bronze"));
		assertTrue(groupColored.get(2).getName().equals("silver"));
		assertTrue(groupColored.get(3).getName().equals("gold"));
		assertTrue(((RealBonus)groupColored.get(3).acquireBonusTile()).getValue() == 20);
		assertTrue(groupColored.get(3).acquireBonusTile().getName().equals("victoryPoint"));
	}

}
