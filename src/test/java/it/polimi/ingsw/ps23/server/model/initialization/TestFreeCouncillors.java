package it.polimi.ingsw.ps23.server.model.initialization;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import it.polimi.ingsw.ps23.server.model.map.GameColor;
import it.polimi.ingsw.ps23.server.model.map.board.FreeCouncillorsSet;
/**
 * Tests the construction of the {@link FreeCouncillorTest} and all methods provided by taht class.
 * @author Mirco Manzoni
 *
 */
public class TestFreeCouncillors {
	
	private static final String TEST_CONFIGURATION_PATH = "src/test/java/it/polimi/ingsw/ps23/server/model/initialization/configuration/";
	private static final String COUNCILLORS_CSV = "councillors.csv";
	
	private FreeCouncillorsSet freeCouncillors;
	
	@Test
	public void test() {
		List<String[]> rawCouncillors = new RawObject(TEST_CONFIGURATION_PATH + COUNCILLORS_CSV).getRawObject();
		freeCouncillors = new CouncillorsBuilder().makeCouncillors(rawCouncillors);
		boolean foundShuffled = false;
		GameColor orange = GameColorsBuilder.makeColor("orange");
		GameColor blue = GameColorsBuilder.makeColor("blue");
		int size = freeCouncillors.getFreeCouncillorsList().size();
		for(int i = 0; i < (size - 1) / 2; i++) {
			if(freeCouncillors.getFreeCouncillorsList().get(i) != freeCouncillors.getFreeCouncillorsList().get(i+1)) {
				foundShuffled = true;
			}
		}
		assertTrue(foundShuffled);
		int countOrange = 0;
		int countBlue = 0;
		for(int i = 0; i < size; i++) {
			GameColor color = freeCouncillors.getFreeCouncillorsList().get(i).getColor();
			if(color.equals(blue)) {
				countBlue++;
			}
			if(color.equals(orange)) {
				countOrange++;
			}
		}
		assertTrue(countBlue == 10 && countOrange == 10);
		assertTrue(freeCouncillors.getFreeCouncillorsList().get(0) == freeCouncillors.getFreeCouncillorsList().remove(0) && size == freeCouncillors.getFreeCouncillorsList().size() + 1);
	}

}
