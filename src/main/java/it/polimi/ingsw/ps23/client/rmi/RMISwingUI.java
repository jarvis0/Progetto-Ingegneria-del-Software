package it.polimi.ingsw.ps23.client.rmi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import it.polimi.ingsw.ps23.client.GUIView;
import it.polimi.ingsw.ps23.client.SwingUI;
import it.polimi.ingsw.ps23.server.model.bonus.Bonus;
import it.polimi.ingsw.ps23.server.model.map.Card;
import it.polimi.ingsw.ps23.server.model.map.Region;
import it.polimi.ingsw.ps23.server.model.map.board.NobilityTrackStep;
import it.polimi.ingsw.ps23.server.model.map.board.PoliticCard;
import it.polimi.ingsw.ps23.server.model.map.regions.BusinessPermitTile;
import it.polimi.ingsw.ps23.server.model.map.regions.City;
import it.polimi.ingsw.ps23.server.model.map.regions.Councillor;
import it.polimi.ingsw.ps23.server.model.map.regions.GroupRegionalCity;
import it.polimi.ingsw.ps23.server.model.map.regions.NormalCity;
import it.polimi.ingsw.ps23.server.model.player.Player;
import it.polimi.ingsw.ps23.server.model.state.StartTurnState;

class RMISwingUI extends SwingUI {
	
	RMISwingUI(GUIView guiView, String mapType, String playerName) {
		super(guiView, mapType, playerName);
	}

	private void freeCouncillorsToStrings(List<Councillor> freeCouncillors, List<String> freeCouncillorsColor) {
		for(int i = 0; i < freeCouncillors.size(); i++) {
			freeCouncillorsColor.add(freeCouncillors.get(i).getColor().toString());
		}
	}

	private void councilsToStrings(List<Region> regions, Queue<Councillor> kingCouncillors, List<String> councilsName, List<List<String>> councilsColor) {
		for(int i = 0; i < regions.size(); i++) {
			GroupRegionalCity region = (GroupRegionalCity) regions.get(i);
			councilsName.add(region.getName());
			Queue<Councillor> councillors = region.getCouncil().getCouncillors();
			List<String> council = new ArrayList<>();
			for(Councillor councillor : councillors) {
				council.add(councillor.getColor().toString());
			}
			councilsColor.add(council);
		}
		List<String> council = new ArrayList<>();
		councilsName.add(getKingdom());
		for(Councillor councillor : kingCouncillors) {
			council.add(councillor.getColor().toString());
		}
		councilsColor.add(council);
	}

	private void bonusToStrings(Bonus bonus, List<String> bonusesName, List<String> bonusesValue) {
		bonusesName.add(bonus.getName());
		bonusesValue.add(String.valueOf(bonus.getValue()));
	}

	private void allPermitTilesToStrings(List<Region> regions, List<String> regionsName, List<List<List<String>>> allPermitTilesCities,
			List<List<List<String>>> allPermitTilesBonusesName, List<List<List<String>>> allPermitTilesBonusesValue) {
		for(Region region : regions) {
			regionsName.add(region.getName());
			List<Card> permitTilesUp = ((GroupRegionalCity) region).getPermitTilesUp().getCards();
			List<List<String>> permitTilesCities = new ArrayList<>();
			List<List<String>> permitTilesBonusesName = new ArrayList<>();
			List<List<String>> permitTilesBonusesValue = new ArrayList<>();
			for(Card card : permitTilesUp) {
				List<String> permitTileCities = new ArrayList<>();
				List<City> cities = ((BusinessPermitTile) card).getCities();
				for(City city : cities) {
					permitTileCities.add(String.valueOf(city.getName().charAt(0)));
				}
				permitTilesCities.add(permitTileCities);
				List<String> permitTileBonusesName = new ArrayList<>();
				List<String> permitTileBonusesValue = new ArrayList<>();
				bonusesToStrings(((BusinessPermitTile) card).getBonuses(), permitTileBonusesName, permitTileBonusesValue);
				permitTilesBonusesName.add(permitTileBonusesName);
				permitTilesBonusesValue.add(permitTileBonusesValue);
			}
			allPermitTilesCities.add(permitTilesCities);
			allPermitTilesBonusesName.add(permitTilesBonusesName);
			allPermitTilesBonusesValue.add(permitTilesBonusesValue);
		}
	}

	private void bonusTilesToStrings(List<Region> regions, List<String> groupsName, List<String> bonusesName, List<String> bonusesValue) {
		for(Region region : regions) {
			if(!region.alreadyUsedBonusTile()) {
				groupsName.add(region.getName());
			}
			else {
				groupsName.add(getAlreadyAcquiredBonusTile());
			}
			bonusToStrings(region.getBonusTile(), bonusesName, bonusesValue);
		}
	}

	private void playersToStrings(List<Player> players, List<String> names, List<String> coins, List<String> assistants,
			List<String> nobilityTrackPoints, List<String> victoryPoints) {
		for(Player player : players) {
			names.add(player.getName());
			coins.add(String.valueOf(player.getCoins()));
			assistants.add(String.valueOf(player.getAssistants()));
			nobilityTrackPoints.add(String.valueOf(player.getNobilityTrackPoints()));
			victoryPoints.add(String.valueOf(player.getVictoryPoints()));
		}
	}

	private void politicCardsToStrings(Map<String, List<String>> playersPoliticCards, List<Player> playersList) {
		for(Player player : playersList) {
			List<String> playerPoliticCards = new ArrayList<>();
			for(Card card : player.getPoliticHandDeck().getCards()) {
				playerPoliticCards.add(((PoliticCard) card).getColor().toString());
			}
			playersPoliticCards.put(player.getName(), playerPoliticCards);
		}
	}

	void refreshDynamicContents(StartTurnState currentState) {
		refreshKingPosition(currentState.getKingPosition());
		List<String> freeCouncillorsColor = new ArrayList<>();
		freeCouncillorsToStrings(currentState.getFreeCouncillors(), freeCouncillorsColor);
		refreshFreeCouncillors(freeCouncillorsColor);
		List<String> councilsName = new ArrayList<>();
		List<List<String>> councilsColor = new ArrayList<>();
		councilsToStrings(currentState.getGroupRegionalCity(), currentState.getKingCouncil().getCouncillors(), councilsName, councilsColor);
		refreshCouncils(councilsName, councilsColor);
		
		List<String> regions = new ArrayList<>();
		List<List<List<String>>> allPermitTilesCities = new ArrayList<>();
		List<List<List<String>>> allPermitTilesBonusesName = new ArrayList<>();
		List<List<List<String>>> allPermitTilesBonusesValue = new ArrayList<>();
		allPermitTilesToStrings(currentState.getGameMap().getGroupRegionalCity(), regions, allPermitTilesCities, allPermitTilesBonusesName, allPermitTilesBonusesValue);
		refreshPermitTilesUp(regions, allPermitTilesCities, allPermitTilesBonusesName, allPermitTilesBonusesValue);
		
		List<String> groupsName = new ArrayList<>();
		List<String> bonusesName = new ArrayList<>();
		List<String> bonusesValue = new ArrayList<>();
		bonusTilesToStrings(currentState.getGroupRegionalCity(), groupsName, bonusesName, bonusesValue);
		List<String> coloredGroupsName = new ArrayList<>();
		List<String> coloredBonusesName = new ArrayList<>();
		List<String> coloredBonusesValue = new ArrayList<>();
		bonusTilesToStrings(currentState.getGroupColoredCity(), coloredGroupsName, coloredBonusesName, coloredBonusesValue);
		Bonus kingTile = currentState.getCurrentKingTile();
		String kingBonusName;
		String kingBonusValue;
		if(kingTile != null) {
			kingBonusName = kingTile.getName();
			kingBonusValue = String.valueOf(kingTile.getValue());
		}
		else {
			kingBonusName = getNoKingTile();
			kingBonusValue = String.valueOf(0);
		}
		groupsName.addAll(coloredGroupsName);
		bonusesName.addAll(coloredBonusesName);
		bonusesValue.addAll(coloredBonusesValue);
		refreshBonusTiles(groupsName, bonusesName, bonusesValue, kingBonusName, kingBonusValue);

		List<Player> players = currentState.getPlayersList();
		List<String> names = new ArrayList<>();
		List<String> coins = new ArrayList<>();
		List<String> assistants = new ArrayList<>();
		List<String> nobilityTrackPoints = new ArrayList<>();
		List<String> victoryPoints = new ArrayList<>();
		playersToStrings(players, names, coins, assistants, nobilityTrackPoints, victoryPoints);
		refreshPlayersTable(names, coins, assistants, nobilityTrackPoints, victoryPoints);
		
		Map<String, List<String>> playersPoliticCards = new HashMap<>();
		politicCardsToStrings(playersPoliticCards, currentState.getPlayersList());
		refreshPoliticCards(playersPoliticCards);		
		
		//TODO
		//refreshAcquiredPermitTiles((currentState.getPlayersList().get(playerIndex)).getPermitHandDeck(), (currentState.getPlayersList().get(playerIndex)).getPermitUsedHandDeck());
		
		getFrame().repaint();
		getFrame().revalidate();
	}

	private void bonusesToStrings(List<Bonus> bonuses, List<String> bonusesName, List<String> bonusesValue) {
		for(int i = 0; i < bonuses.size(); i++)  {
			bonusToStrings(bonuses.get(i), bonusesName, bonusesValue);
		}
	}
	
	private void rewardTokensToStrings(Map<String, City> cityMap, List<String> citiesName, List<List<String>> citiesBonusesName, List<List<String>> citiesBonusesValue) {
		Set<Entry<String, City>> citiesEntries = cityMap.entrySet();
		for(Entry<String, City> cityEntry : citiesEntries) {
			City city = cityEntry.getValue();
			if(!city.isCapital()) {
				citiesName.add(city.getName());
				List<Bonus> bonuses = ((NormalCity) city).getRewardToken().getBonuses();
				List<String> bonusesName = new ArrayList<>();
				List<String> bonusesValue = new ArrayList<>();
				bonusesToStrings(bonuses, bonusesName, bonusesValue);
				citiesBonusesName.add(bonusesName);
				citiesBonusesValue.add(bonusesValue);
			}
		}
	}

	private void nobilityTrackToStrings(List<NobilityTrackStep> steps, List<List<String>> stepsBonusesName, List<List<String>> stepsBonusesValue) {
		for(NobilityTrackStep step : steps) {
			List<Bonus> bonuses = step.getBonuses();		
			List<String> bonusesName = new ArrayList<>();
			List<String> bonusesValue = new ArrayList<>();
			bonusesToStrings(bonuses, bonusesName, bonusesValue);
			stepsBonusesName.add(bonusesName);
			stepsBonusesValue.add(bonusesValue);
		}
	}

	void loadStaticContents(StartTurnState currentState) {
		List<String> citiesName = new ArrayList<>();
		List<List<String>> citiesBonusesName = new ArrayList<>();
		List<List<String>> citiesBonusesValue = new ArrayList<>();
		rewardTokensToStrings(currentState.getGameMap().getCities(), citiesName, citiesBonusesName, citiesBonusesValue);
		addRewardTokens(citiesName, citiesBonusesName, citiesBonusesValue);
		
		List<List<String>> stepsBonusesName = new ArrayList<>();
		List<List<String>> stepsBonusesValue = new ArrayList<>();
		nobilityTrackToStrings(currentState.getNobilityTrack().getSteps(), stepsBonusesName, stepsBonusesValue);
		addNobilityTrackBonuses(stepsBonusesName, stepsBonusesValue);
	}
	
	/*private void refreshAcquiredPermitTiles(HandDeck permissionHandDeck) {
		for(JLabel permitTile : playerPermitTiles) {
			getMapPanel().remove(permitTile);
		}
		List<Card> permitHandDeckList = permissionHandDeck.getCards();
		int indexOfTile = 0;
		int x = 0;
		int y = 611;
		for(Card permitTile : permitHandDeckList) {
			//drawPermitTile(permitTile, indexOfTile, playerPermitTiles, x, y);
			x += 52;
		}
	}*/

}
