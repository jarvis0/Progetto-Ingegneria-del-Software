package it.polimi.ingsw.ps23.server.model.bonus;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import it.polimi.ingsw.ps23.server.model.Game;
import it.polimi.ingsw.ps23.server.model.TurnHandler;
/**
  * Tests all the changes made by {@link NobilityTrackBonus} to the {@link Player} status.
 * @author Mirco Manzoni
 *
 */
public class TrackNobilityTrackStepBonus {

	@Test
	public void test() {
		List<String> playersName = new ArrayList<>();
		playersName.add("a");
		Game game = new Game(playersName);
		game.setCurrentPlayer(game.getGamePlayersSet().getPlayers().get(0));
		TurnHandler turnHandler = new TurnHandler();
		NobilityTrackStepBonus bonus = new NobilityTrackStepBonus("Nobility Track Step Bonus");
		int initialnobilityPoints = game.getCurrentPlayer().getNobilityTrackPoints();
		bonus.updateBonus(game, turnHandler);
		assertTrue(game.getCurrentPlayer().getNobilityTrackPoints() == initialnobilityPoints + bonus.getValue());
	}

}
