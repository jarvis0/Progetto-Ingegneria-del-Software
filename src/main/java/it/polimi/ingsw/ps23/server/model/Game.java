package it.polimi.ingsw.ps23.server.model;

import java.util.List;

import it.polimi.ingsw.ps23.server.model.initialization.Initialization;
import it.polimi.ingsw.ps23.server.model.map.Deck;
import it.polimi.ingsw.ps23.server.model.map.GameMap;
import it.polimi.ingsw.ps23.server.model.map.board.FreeCouncillorsSet;
import it.polimi.ingsw.ps23.server.model.map.board.King;
import it.polimi.ingsw.ps23.server.model.map.board.KingTileSet;
import it.polimi.ingsw.ps23.server.model.map.board.NobilityTrack;
import it.polimi.ingsw.ps23.server.model.market.Market;
import it.polimi.ingsw.ps23.server.model.player.Player;
import it.polimi.ingsw.ps23.server.model.player.PlayersSet;
import it.polimi.ingsw.ps23.server.model.state.StateCache;

public class Game {

	private Deck politicDeck;
	private FreeCouncillorsSet freeCouncillors;
	private GameMap gameMap;
	private King king;
	private KingTileSet kingTiles;
	private NobilityTrack nobilityTrack;
	private PlayersSet playersSet;
	private Player currentPlayer;
	private Market currentMarket;

	public Game(List<String> playersName) throws NoCapitalException {
		Initialization init = new Initialization(playersName);
		politicDeck = init.getPoliticDeck();
		freeCouncillors = init.getFreeCouncillors();
		gameMap = init.getGameMap();
		king = init.getKing();
		kingTiles = init.getKingTiles();
		nobilityTrack = init.getNobilityTrack();
		playersSet = init.getPlayersSet();
		StateCache.loadCache();//TODO spostare questa istruzione
	}
	
	public GameMap getGameMap() {
		return gameMap;
	}
	
	public PlayersSet getGamePlayersSet() {
		return playersSet;
	}

	public Deck getPoliticDeck() {
		return politicDeck;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public FreeCouncillorsSet getFreeCouncillors() {
		return freeCouncillors;
	}
	
	public King getKing() {
		return king;
	}
	 
	public KingTileSet getKingTileSet() {
		return kingTiles;
	}

	public NobilityTrack getNobilityTrack() {
		return nobilityTrack;
	}

	public void createNewMarket() {
		currentMarket = new Market(playersSet);
	}
	
	public int getNumberOfPlayer() {
		return playersSet.numberOfPlayer();
	}
	
	public Market getMarket() {
		return currentMarket;
	}
}