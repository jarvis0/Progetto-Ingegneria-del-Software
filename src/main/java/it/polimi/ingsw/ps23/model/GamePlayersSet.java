package it.polimi.ingsw.ps23.model;

import java.util.ArrayList;
import java.util.List;

//forse meglio HashMap ?
public class GamePlayersSet {
	
	private List<Player> players;

	public GamePlayersSet() {
		players = new ArrayList<>();
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}

	public List<Player> getPlayers() {
		return players;
	}
	
	public Player getPlayer(int index) {
		return players.get(index);
	}
	
	@Override
	public String toString() {
		return players.toString();
	}
	
}