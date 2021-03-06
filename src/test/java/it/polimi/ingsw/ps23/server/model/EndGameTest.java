package it.polimi.ingsw.ps23.server.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;

import it.polimi.ingsw.ps23.server.model.EndGame;
import it.polimi.ingsw.ps23.server.model.Game;
import it.polimi.ingsw.ps23.server.model.TurnHandler;
import it.polimi.ingsw.ps23.server.model.WinnerComparator;
import it.polimi.ingsw.ps23.server.model.map.Card;
import it.polimi.ingsw.ps23.server.model.map.regions.BusinessPermitTile;
import it.polimi.ingsw.ps23.server.model.map.regions.City;
import it.polimi.ingsw.ps23.server.model.player.Player;
/**
 * Tests the condition of the end game and all methods to take all the {@link BonusTile} that players have
 * gained during the game. There are also tested the {@link WinnerComparator} and other comparator to determinate
 * the winner of the current game.
 * @author Mirco Manzoni
 *
 */
public class EndGameTest {

	@Test
	public void test() {
		List<String> players = new ArrayList<>();
		players.add("Player 1");
		players.add("Player 2");
		players.add("Player 3");
		Game game = new Game(players);
		TurnHandler turnHandler = new TurnHandler();
		Player winner = game.getGamePlayersSet().getPlayer(0);
		Player second = game.getGamePlayersSet().getPlayer(2);
		Player third = game.getGamePlayersSet().getPlayer(1);
		game.setCurrentPlayer(winner);
		winner.updateVictoryPoints(100);
		EndGame endGame = new EndGame(game, turnHandler);
		Iterator<Entry<String,City>> iterator = game.getGameMap().getCities().entrySet().iterator();
		while(!winner.hasFinished() && iterator.hasNext()) {
			assertFalse(endGame.isGameEnded());
			winner.getEmporiums().getBuiltEmporiumsSet().add(iterator.next().getValue());			
		}
		assertTrue(endGame.isGameEnded());
		for(Player player : game.getGamePlayersSet().getPlayers()) {
			if(!player.equals(winner)) {
				assertTrue(new WinnerComparator().compare(winner, player) >= 1);
			}
		}
		assertTrue(new WinnerComparator().compare(second, third) >= 1);
		second.updateNobilityPoints(10);
		List<Card> permissionCards = new ArrayList<>();
		permissionCards.add(new BusinessPermitTile());
		third.buyPermitCards(permissionCards);
		third.buyPermitCards(permissionCards);
		second.buyPermitCards(permissionCards);
		third.updateNobilityPoints(10);
		second.updateNobilityPoints(2);		
		second.updateVictoryPoints(- second.getVictoryPoints());
		third.updateVictoryPoints(- third.getVictoryPoints());
		game.setCurrentPlayer(winner);
		endGame.isGameEnded();
		assertTrue(new WinnerComparator().compare(second, third) >= 1);
	}

}
