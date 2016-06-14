package it.polimi.ingsw.ps23.server.model.player;

import java.util.Deque;

import it.polimi.ingsw.ps23.server.model.bonus.Bonus;

public class KingTileSet {
	
	private Deque<Bonus> kingTiles;
	
	public KingTileSet(Deque<Bonus> tilesStack) {
		this.kingTiles = tilesStack;
	}
	
	Bonus pop() {
		return kingTiles.pop();
	}
	
	boolean isEmpty() {
		return kingTiles.isEmpty();
	}
	
	@Override
	public String toString() {
		return kingTiles.toString();
	}
	
}
