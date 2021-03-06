package it.polimi.ingsw.ps23.server.model.initialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import it.polimi.ingsw.ps23.server.model.bonus.BonusCache;
import it.polimi.ingsw.ps23.server.model.bonus.BonusSlot;
import it.polimi.ingsw.ps23.server.model.map.Card;
import it.polimi.ingsw.ps23.server.model.map.Deck;
import it.polimi.ingsw.ps23.server.model.map.regions.City;
import it.polimi.ingsw.ps23.server.model.map.regions.BusinessPermitTile;

class PermitTilesBuilder {

	private static final int BONUSES_NUMBER = 6;
	
	private List<String[]> rawPermissionCards;
	private Map<String, City> cities;
	
	PermitTilesBuilder(List<String[]> rawPermissionCards, Map<String, City> cities) {
		super();
		this.rawPermissionCards = rawPermissionCards;
		this.cities = cities;
	}
	
	private String[] subString(String[] rawPermissionCard) {
		String[] rawBonuses = new String[BONUSES_NUMBER];
		for(int i = 0; i < BONUSES_NUMBER; i++) {
			rawBonuses[i] = rawPermissionCard[i];
		}
		return rawBonuses;
	}
	
	private BonusSlot addCitiesToPermissionCard(String[] rawPermissionCard) {
		StringBuilder cityName;
		BonusSlot permissionCard = new BusinessPermitTile();
		for(int i = BONUSES_NUMBER + 1; i < rawPermissionCard.length; i++) {
			cityName = new StringBuilder();
			cityName.append(cityName + rawPermissionCard[i]);
			((BusinessPermitTile) permissionCard).addCity(cities.get(new String() + cityName));
		}
		return permissionCard;
	}

	private Map<String, Deck> toDecks(Map<String, List<Card>> cardsMap) {
		Map<String, Deck> permissionDecks = new HashMap<>();
		Set<Entry<String, List<Card>>> cardsMapEntrySet = cardsMap.entrySet();
		for(Entry<String, List<Card>> decks : cardsMapEntrySet) {
			permissionDecks.put(decks.getKey(), new Deck(decks.getValue()));
		}
		return permissionDecks;
	}
	
	Map<String, Deck> makeDecks(BonusCache bonusCache) {
		List<Card> permissionCards;
		Map<String, List<Card>> cardsMap = new HashMap<>();
		String[] fields = rawPermissionCards.remove(rawPermissionCards.size() - 1);
		for(String[] rawPermissionCard : rawPermissionCards) {
			String[] rawBonuses = subString(rawPermissionCard);
			BonusSlot permissionCard = addCitiesToPermissionCard(rawPermissionCard);
			String regionName = rawPermissionCard[BONUSES_NUMBER];
			if(!cardsMap.containsKey(regionName)) {
				permissionCards = new ArrayList<>();
				cardsMap.put(regionName, permissionCards);
			}
			cardsMap.get(regionName).add((Card) new BonusesBuilder(bonusCache).makeBonuses(fields, rawBonuses, permissionCard));
		}
		return toDecks(cardsMap);
	}

}
