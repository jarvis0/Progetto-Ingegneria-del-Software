package it.polimi.ingsw.ps23.server.model;

import java.io.Serializable;
import java.util.List;

import it.polimi.ingsw.ps23.server.model.initialization.Initialization;
import it.polimi.ingsw.ps23.server.model.map.Deck;
import it.polimi.ingsw.ps23.server.model.map.GameMap;
import it.polimi.ingsw.ps23.server.model.map.board.FreeCouncillorsSet;
import it.polimi.ingsw.ps23.server.model.map.board.King;
import it.polimi.ingsw.ps23.server.model.map.board.NobilityTrack;
import it.polimi.ingsw.ps23.server.model.market.Market;
import it.polimi.ingsw.ps23.server.model.player.KingTilesSet;
import it.polimi.ingsw.ps23.server.model.player.Player;
import it.polimi.ingsw.ps23.server.model.player.PlayersSet;
import it.polimi.ingsw.ps23.server.model.state.StateCache;

/**
 * This class provides all game components and their relative
 * getters.
 * @author Alessandro Erba & Giuseppe Mascellaro & Mirco Manzoni
 *
 */
public class Game implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9200411232887252524L;
	private String mapType;
	private Deck politicDeck;
	private FreeCouncillorsSet freeCouncillors;
	private GameMap gameMap;
	private King king;
	private KingTilesSet kingTiles;
	private NobilityTrack nobilityTrack;
	private PlayersSet playersSet;
	private Player currentPlayer;
	private Market currentMarket;
	private StateCache stateCache;
	private boolean lastEmporiumBuilt;

	/**
	 * Create a new game initialization object taking game player names
	 * and then stores all references to game resources in class attributes.
	 * <p>
	 * Useful to split game initialization part (with raw configuration files) from
	 * an higher level game representation of resources.
	 * @param playerNames - to be part of the new creating game
	 */
	public Game(List<String> playerNames) {
		Initialization init = new Initialization(playerNames);
		mapType = init.getChosenMap();
		politicDeck = init.getPoliticDeck();
		freeCouncillors = init.getFreeCouncillors();
		gameMap = init.getGameMap();
		king = init.getKing();
		kingTiles = init.getKingTiles();
		nobilityTrack = init.getNobilityTrack();
		playersSet = init.getPlayersSet();
		stateCache = new StateCache();
		lastEmporiumBuilt = false;
	}
	
	public String getMapType() {
		return mapType;
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
	 
	public KingTilesSet getKingTilesSet() {
		return kingTiles;
	}

	public NobilityTrack getNobilityTrack() {
		return nobilityTrack;
	}

	public StateCache getStateCache() {
		return stateCache;
	}
	
	public void createNewMarket() {
		currentMarket = new Market(playersSet);
	}
	
	public int getPlayersNumber() {
		return playersSet.playersNumber();
	}
	
	public int getMarketPlayersNumber() {
		return playersSet.marketPlayersNumber();
	}
	
	public Market getMarket() {
		return currentMarket;
	}
	
	public void lastEmporiumBuilt() {
		lastEmporiumBuilt = true;
	}
	
	public boolean canTakeBonusLastEmporium() {
		return lastEmporiumBuilt;
	}
	
}
