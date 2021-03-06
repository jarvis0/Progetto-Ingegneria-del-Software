package it.polimi.ingsw.ps23.server.model.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidCardException;
import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidCityException;
import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidRegionException;
import it.polimi.ingsw.ps23.server.model.Game;
import it.polimi.ingsw.ps23.server.model.TurnHandler;
import it.polimi.ingsw.ps23.server.model.bonus.Bonus;
import it.polimi.ingsw.ps23.server.model.bonus.BuildingPermitBonus;
import it.polimi.ingsw.ps23.server.model.bonus.RealBonus;
import it.polimi.ingsw.ps23.server.model.bonus.RecycleBuildingPermitBonus;
import it.polimi.ingsw.ps23.server.model.bonus.RecycleRewardTokenBonus;
import it.polimi.ingsw.ps23.server.model.bonus.SuperBonus;
import it.polimi.ingsw.ps23.server.model.bonus.SuperBonusGiver;
import it.polimi.ingsw.ps23.server.model.map.Region;
import it.polimi.ingsw.ps23.server.model.map.regions.GroupRegionalCity;
import it.polimi.ingsw.ps23.server.model.player.Player;
import it.polimi.ingsw.ps23.server.view.ViewVisitor;
/**
 * Provides methods to gathering all the info to apply a {@link SuperBonus}.
 * @author Alessandro Erba & Mirco Manzoni
 *
 */
public class SuperBonusState extends MapUpdateState {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6033290786328005099L;
	private TurnHandler turnHandler;
	private List<Bonus> superBonus;
	private Bonus currentBonus;
	private Player currentPlayer;
	private boolean exceptionOccured;
	private Map<Bonus, List<String>> selectedBonuses;
	private List<String> bonusesSelections;
	private Map<String, Region> regionsMap;
	private String selectedRegion;
	/**
	 * Constructs the objects initializing all the variables to the default values.
	 * @param turnHandler - current turn handler with all the {@link SuperBonus} occurred
	 */
	public SuperBonusState(TurnHandler turnHandler) {
		 this.turnHandler = turnHandler;
		 superBonus = new ArrayList<>();
		 selectedBonuses = new HashMap<>();
		 exceptionOccured = false;
	}
	/**
	 * Calculates if there are {@link SuperBonus} that haven't already been considered.
	 * @return true if there are other superbonuses, false otherwise
	 */
	public boolean hasNext() {
		return !superBonus.isEmpty();
	}
	/**
	 * Creates a string characterized by the elements of the current {@link SuperBonus}
	 * @return the created string
	 */
	public String useBonus() {
		return ((SuperBonus) currentBonus).checkBonus(currentPlayer);
	}
	/**
	 * Get the value of the {@link SuperBonus} that is being considered in the SuperBonusState visit.
	 * @return the value of the current bonus
	 */

	public int getCurrentBonusValue() {
		if(exceptionOccured) {
			exceptionOccured = false;
		}
		currentBonus = superBonus.remove(superBonus.size() - 1);
		return ((RealBonus)currentBonus).getValue();
	}
	/**
	 * Creates the object to manipulate all the {@link SuperBonus}.
	 * @return the object created
	 */
	public SuperBonusGiver createSuperBonusesGiver() {
		return new SuperBonusGiver(selectedBonuses);
	}
	/**
	 * Analyzes the super bonus runtime instance.
	 * @return true if the bonus analyzed in the view is a {@link BuildingPermitBonus}
	 */
	public boolean isBuildingPemitTileBonus() {
		return currentBonus instanceof BuildingPermitBonus;
	}
	/**
	 * Analyzes the super bonus runtime instance.
	 * @return true if the bonus analyzed in the view is a {@link RecycleBuildingPermitBonus}
	 */
	public boolean isRecycleBuildingPermitBonus() {
		return currentBonus instanceof RecycleBuildingPermitBonus;
	}
	/**
	 * Analyzes the super bonus runtime instance.
	 * @return true if the bonus analyzed in the view is a {@link RecycleRewardTokenBonus}
	 */	
	public boolean isRecycleRewardTokenBonus() {
		return currentBonus instanceof RecycleRewardTokenBonus;
	}

	/**
	 * Analyzes the input region and adds to the list of inputs the object needs.
	 * @param chosenRegion - selected region
	 * @throws InvalidRegionException if an invalid region has been selected
	 */
	public void analyzeInput(String chosenRegion) throws InvalidRegionException {
		((BuildingPermitBonus) currentBonus).selectRegion(chosenRegion);
		selectedRegion = chosenRegion;
		bonusesSelections.add(selectedRegion);
	}
	/**
	 * Checks if a type of {@link SuperBonus} has already saved in this objects. If there are inputs saved
	 * for that bonus, the method opens that saved inputs to append new inputs.
	 */
	public void checkKey() {
		bonusesSelections = new ArrayList<>();
		if (selectedBonuses.containsKey(currentBonus)) {
			bonusesSelections = selectedBonuses.get(currentBonus);
		}
	}
	/**
	 * Adds the selected input to the list of input needed.
	 * @param value - the current input
	 * @throws InvalidCityException if an invalid city has been selected
	 * @throws InvalidCardException if an invalid card has been selected
	 */
	public void addValue(String value) throws InvalidCityException, InvalidCardException {
		exceptionOccured = true;
		if(currentBonus instanceof RecycleRewardTokenBonus) {
			currentPlayer.getEmporiumForRecycleRewardToken().getChosenCity(value);
		}
		if(currentBonus instanceof RecycleBuildingPermitBonus) {
			currentPlayer.getAllPermitHandDeck().getCardInPosition(Integer.parseInt(value) - 1);
		}
		if(currentBonus instanceof BuildingPermitBonus && (((GroupRegionalCity)(regionsMap.get(selectedRegion))).getPermitTilesUp().getCards().size() - 1 < Integer.parseInt(value) - 1 || Integer.parseInt(value) - 1 < 0)) {
				throw new InvalidCardException();
		}
		bonusesSelections.add(value);
		exceptionOccured = false;
	}
	/**
	 * Confirms the input saving definitely all the inputs inserted. 
	 */
	public void confirmChange() {
		selectedBonuses.put(currentBonus, bonusesSelections);		
	}

	@Override
	public void changeState(Context context, Game game) {
		context.setState(this);
		setParameters(game);
		superBonus.addAll(turnHandler.getSuperBonuses());
		currentPlayer = game.getCurrentPlayer();
		regionsMap = game.getGameMap().getRegionMap();
	}

	@Override
	public void acceptView(ViewVisitor view) {
		view.visit(this);
	}
	
}	
	