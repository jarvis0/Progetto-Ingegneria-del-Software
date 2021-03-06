package it.polimi.ingsw.ps23.server.model.player;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import it.polimi.ingsw.ps23.server.model.map.Card;
import it.polimi.ingsw.ps23.server.model.map.Deck;
import it.polimi.ingsw.ps23.server.model.map.GameColor;
import it.polimi.ingsw.ps23.server.model.map.board.PoliticCard;
import it.polimi.ingsw.ps23.server.model.player.Player;
import it.polimi.ingsw.ps23.server.model.player.PlayersSet;
import it.polimi.ingsw.ps23.server.model.player.PoliticHandDeck;
/**
 * Tests the {@link PlayerSet}. In particular the way how the method manage th players during the game.
 * @author Mirco Manzoni
 *
 */
public class TestPlayerSet {

	@Test
	public void test() {
		List<Card> cards = new ArrayList<>();
		GameColor gameColor = new GameColor("orange");
		cards.add(new PoliticCard(gameColor));
		Deck politicDeck = new Deck(cards);
		Player player = new Player("1", 2, 2, new PoliticHandDeck(politicDeck.pickCards(1)));
		PlayersSet playersSet = new PlayersSet();
		playersSet.addPlayer(player);
		assertTrue(playersSet.getPlayer(0).equals(player));
		assertTrue(playersSet.getPlayers().get(0).equals(player));
		assertTrue(playersSet.playersNumber() == 1);
		assertTrue(playersSet.marketPlayersNumber() == 1);
		assertTrue(playersSet.toString().contains("1"));
	}

}
