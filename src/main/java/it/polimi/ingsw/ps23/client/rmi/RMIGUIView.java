package it.polimi.ingsw.ps23.client.rmi;

import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.polimi.ingsw.ps23.client.GUIView;
import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidCardException;
import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidCityException;
import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidCostException;
import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidNumberOfAssistantException;
import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidRegionException;
import it.polimi.ingsw.ps23.server.model.actions.Action;
import it.polimi.ingsw.ps23.server.model.player.Player;
import it.polimi.ingsw.ps23.server.model.state.AcquireBusinessPermitTileState;
import it.polimi.ingsw.ps23.server.model.state.AdditionalMainActionState;
import it.polimi.ingsw.ps23.server.model.state.AssistantToElectCouncillorState;
import it.polimi.ingsw.ps23.server.model.state.BuildEmporiumKingState;
import it.polimi.ingsw.ps23.server.model.state.BuildEmporiumPermitTileState;
import it.polimi.ingsw.ps23.server.model.state.ChangePermitTilesState;
import it.polimi.ingsw.ps23.server.model.state.ElectCouncillorState;
import it.polimi.ingsw.ps23.server.model.state.EndGameState;
import it.polimi.ingsw.ps23.server.model.state.EngageAnAssistantState;
import it.polimi.ingsw.ps23.server.model.state.MarketBuyPhaseState;
import it.polimi.ingsw.ps23.server.model.state.MarketOfferPhaseState;
import it.polimi.ingsw.ps23.server.model.state.StartTurnState;
import it.polimi.ingsw.ps23.server.model.state.State;
import it.polimi.ingsw.ps23.server.model.state.SuperBonusState;

public class RMIGUIView extends RMIView implements GUIView {
	
	private static final String CANNOT_REACH_SERVER_PRINT = "Cannot reach remote server.";
	
	private RMISwingUI swingUI;
	private PrintStream output;
	private State state;
	private boolean endGame;
	private boolean waiting;
	private boolean firstUIRefresh;
	
	RMIGUIView(String playerName, PrintStream output) {
		super(playerName);
		firstUIRefresh = true;
		this.output = output;
	}

	public State getCurrentState() {
		return state;
	}

	@Override
	void setMapType(String mapType) {
		output.print("\nMap type: " + mapType + ".");
		swingUI = new RMISwingUI(this, mapType, getClientName());
	}

	@Override
	public void visit(StartTurnState currentState) {
		if(firstUIRefresh) {
			swingUI.loadStaticContents(currentState);
			firstUIRefresh = false;
		}
		swingUI.refreshDynamicContents(currentState);
		Player player = currentState.getCurrentPlayer();
		if(player.getName().equals(getClientName())) {
			swingUI.appendConsoleText("\nIt's your turn, please select an action from the pool displayed above.");
			swingUI.showAvailableActions(currentState.isAvailableMainAction(), currentState.isAvailableQuickAction());
			pause();
			try {
				getControllerInterface().wakeUpServer(currentState.getStateCache().getAction(swingUI.getChosenAction()));
			} catch (RemoteException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, CANNOT_REACH_SERVER_PRINT, e);
			}
		} else {
			swingUI.setConsoleText("\nIt's " + currentState.getCurrentPlayer().getName() + "'s turn.");
			swingUI.showAvailableActions(false, false);
			waiting = true;
			pause();
		}
	}

	@Override
	public void visit(ElectCouncillorState currentState) {
		swingUI.clearSwingUI();
		swingUI.showAvailableActions(false, false);
		swingUI.enableFreeCouncillorsButtons(true);
		swingUI.appendConsoleText("\n\nYou are performing a Elect councillor main action,\npress on a free councillor to select it.");
		pause();
		String chosenCouncillor = swingUI.getChosenCouncillor();
		swingUI.appendConsoleText("\nYou have chosen a " + chosenCouncillor + " Councillor,\nPress on the region where you want to put it ");
		swingUI.enableFreeCouncillorsButtons(false);
		swingUI.enableRegionButtons(true);
		swingUI.enableKingButton(true);
		pause();
		String chosenBalcony = swingUI.getChosenRegion();
		swingUI.appendConsoleText("\nYou have just elected a " + chosenCouncillor + " councillor in " + chosenBalcony + "'s balcony");
		sendAction(currentState.createAction(chosenCouncillor, chosenBalcony));

	}

	@Override
	public void visit(EngageAnAssistantState currentState) {
		swingUI.appendConsoleText("\n\nYou are performing a Engage An Assistant quick action");
		sendAction(currentState.createAction());

	}

	@Override
	public void visit(ChangePermitTilesState currentState) {
		swingUI.enableRegionButtons(true);
		swingUI.appendConsoleText("\n\nYou are performing a Change Permits Tile quick action,\n please select the region where you what to change tiles.");
		pause();
		String chosenRegion = swingUI.getChosenRegion();
		swingUI.enableRegionButtons(false);
		swingUI.appendConsoleText("\nYou have just changed the " + chosenRegion + "'s Permit Tiles");
		sendAction(currentState.createAction(chosenRegion));
	}

	@Override
	public void visit(AcquireBusinessPermitTileState currentState) {
		try {
			swingUI.clearSwingUI();
			swingUI.showAvailableActions(false, false);
			swingUI.enableRegionButtons(true);
			swingUI.enableFinish(false);
			swingUI.appendConsoleText("\n\nYou are performing a Acquire Business Permit Tile main action,\npress on the region whose council you whant to satisfy");
			List<String> removedCards = new ArrayList<>();
			pause();
			swingUI.enableRegionButtons(false);
			String chosenCouncil = swingUI.getChosenRegion();
			swingUI.appendConsoleText("\nYou have selected the " + chosenCouncil + " council,\npress on the politic cards that you what to use for satisfy this council");
			swingUI.enablePoliticCards(true);
			int numberOfCards = 4;
			boolean finish = false;
			int i = 0;
			while (i < numberOfCards && i < currentState.getPoliticHandSize() && !finish) {
				pause();
				finish = swingUI.hasFinished();
				swingUI.enableFinish(true);
				if(!finish) {
					removedCards.add(swingUI.getChosenCard());
				}
				i++;
			}
			swingUI.appendConsoleText("\nYou have selected these politic cards:\n" + removedCards.toString() + "\nyou can now press on the permit tile that you want acquire.");
			swingUI.enablePoliticCards(false);
			swingUI.enablePermitTilesPanel(chosenCouncil);
			pause();
			int chosenTile = swingUI.getChosenTile();
			sendAction(currentState.createAction(chosenCouncil, removedCards, chosenTile));
		} catch (InvalidCardException | NumberFormatException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), e);
			state.setExceptionString(e.toString());
		}

	}

	@Override
	public void visit(AssistantToElectCouncillorState currentState) {
		swingUI.clearSwingUI();
		swingUI.appendConsoleText("\n\nYou are performing an Assistant To Elect Councillor quick action,\npress on a free councillor to select it.");
		swingUI.showAvailableActions(false, false);
		swingUI.enableFreeCouncillorsButtons(true);
		pause();
		String chosenCouncillor = swingUI.getChosenCouncillor();
		swingUI.appendConsoleText("\nYou have chosen a " + chosenCouncillor + " Councillor,\npress on the region where you want to put it.");
		swingUI.enableFreeCouncillorsButtons(false);
		swingUI.enableRegionButtons(true);
		swingUI.enableKingButton(true);
		pause();
		String chosenBalcony = swingUI.getChosenRegion();
		swingUI.appendConsoleText("\nYou have just elected a " + chosenCouncillor + "councillor in " + chosenBalcony + "'s balcony");
		sendAction(currentState.createAction(chosenCouncillor, chosenBalcony));
	}

	@Override
	public void visit(AdditionalMainActionState currentState) {
		swingUI.appendConsoleText("\n\nYou are performing an Additional Main Action quick action");
		sendAction(currentState.createAction());
	}

	@Override
	public void visit(BuildEmporiumKingState currentState) {
		List<String> removedCards = new ArrayList<>();
		swingUI.showAvailableActions(false, false);
		swingUI.enablePoliticCards(true);
		swingUI.enableFinish(false);
		swingUI.appendConsoleText("\n\nYou are performing a Build Emporium King Main Action,\npress on the politic cards thet you want to use for satisfy the King's council.");
		int numberOfCards = 4;
		boolean finish = false;
		int i = 0;
		while (i < numberOfCards && i < currentState.getPoliticHandSize() && !finish) {
			pause();
			finish = swingUI.hasFinished();
			swingUI.enableFinish(true);
			if(!finish) {
				removedCards.add(swingUI.getChosenCard());
			}
			i++;
		}
		swingUI.appendConsoleText("\nYou have selected these politic cards:\n" + removedCards.toString() + "\nplease press on the city where you want to move the King.");
		swingUI.enablePoliticCards(false);
		swingUI.enableCities(true);
		pause();
		swingUI.enableCities(false);
		String arrivalCity = swingUI.getChosenCity();
		try {
			sendAction(currentState.createAction(removedCards, arrivalCity));
		} catch (InvalidCardException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), e);
			state.setExceptionString(e.toString());
		}	
	}


	@Override
	public void visit(BuildEmporiumPermitTileState currentState) {
		swingUI.clearSwingUI();
		swingUI.showAvailableActions(false, false);
		swingUI.enablePermitTileDeck(true);
		swingUI.appendConsoleText("\n\nYou are performing a Build Emporium Permit Tile Main Action,\npress on the permit tile that you want use.");
		pause();
		int chosenCard = swingUI.getChosenTile();
		swingUI.appendConsoleText("\nYou have chose tile number: " + chosenCard + "\npress on the city want to build.");
		swingUI.enableCities(true);
		pause();
		swingUI.enableCities(false);
		String chosenCity = swingUI.getChosenCity();
		sendAction(currentState.createAction(chosenCity, chosenCard));
	}
	
	private List<String> sellPoliticCard(MarketOfferPhaseState currentState) throws NumberFormatException {
		List<String> chosenPoliticCards = new ArrayList<>();
		if (currentState.canSellPoliticCards()) {
			swingUI.setConsoleText("\nHow many politic cards do you whant to sell? ");
			swingUI.enableMarketInputArea(true);
			pause();
			int numberOfCards = swingUI.getChosenValue();
			swingUI.enableMarketInputArea(false);
			swingUI.enablePoliticCards(true);
			swingUI.setConsoleText("\nplease press on the cards that you whant to sell");
			for (int i = 0; i < numberOfCards && i < currentState.getPoliticHandSize(); i++) {
				pause();
				chosenPoliticCards.add(swingUI.getChosenCard());
			}
		}
		return chosenPoliticCards;
	}
	
	private List<Integer> sellPermissionCard(MarketOfferPhaseState currentState) throws NumberFormatException {
		List<Integer> chosenPermissionCards = new ArrayList<>();
		if (currentState.canSellPermissionCards()) {
			swingUI.setConsoleText("\nHow many permission cards do you want to use? (numerical input >0)");
			swingUI.enableMarketInputArea(true);
			pause();
			int numberOfCards = swingUI.getChosenValue();
			swingUI.enableMarketInputArea(false);
			swingUI.enablePermitTileDeck(true);
			swingUI.setConsoleText("\nplease press on the cards that you whant to sell");
			for (int i = 0; i < numberOfCards && i < currentState.getPermissionHandSize(); i++) {
				pause();
				chosenPermissionCards.add(swingUI.getChosenTile());
			}
		}
		return chosenPermissionCards;
	}
	
	private int sellAssistant(MarketOfferPhaseState currentState) throws NumberFormatException {
		int chosenAssistants = 0;
		if (currentState.canSellAssistants()) {
			swingUI.setConsoleText("\nSelect the number of assistants " + currentState.getAssistants());
			swingUI.enableMarketInputArea(true);
			pause(); 
			chosenAssistants = swingUI.getChosenValue();
		}
		return chosenAssistants;
	}
	
	@Override
	public void visit(MarketOfferPhaseState currentState) {
		String player = currentState.getPlayerName();
		swingUI.setConsoleText("\nIt's " + player + " market phase turn.");
		if (player.equals(getClientName())) {
			List<String> chosenPoliticCards = sellPoliticCard(currentState);
			List<Integer> chosenPermissionCards = sellPermissionCard(currentState);
			int chosenAssistants = sellAssistant(currentState);
			swingUI.setConsoleText("\nChoose the price for your offer: ");
			swingUI.enableMarketInputArea(true);
			pause(); 
			int cost = swingUI.getChosenValue();
			swingUI.enableMarketInputArea(false);
			try {
				getControllerInterface().wakeUpServer(currentState.createMarketObject(chosenPoliticCards,
							chosenPermissionCards, chosenAssistants, cost));
			} catch (RemoteException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, CANNOT_REACH_SERVER_PRINT, e);
			} catch (InvalidCardException | InvalidNumberOfAssistantException | InvalidCostException | NumberFormatException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), e);
				state.setExceptionString(e.toString());
			}
		} else {
			waiting = true;
			pause();
		}
	}

	@Override
	public void visit(MarketBuyPhaseState currentState) {
		String player = currentState.getPlayerName();
		swingUI.setConsoleText("\nIt's " + player + " market phase turn.");
		if(player.equals(getClientName())) {
			try {
				if (currentState.canBuy()) {
					swingUI.setConsoleText("\nChoose the offert that you want to buy: \n" + currentState.getAvaiableOffers());
					swingUI.enableMarketInputArea(true);
					pause();
					int chosenOffert = swingUI.getChosenValue();
					swingUI.enableMarketInputArea(false);
					getControllerInterface().wakeUpServer(currentState.createTransation(chosenOffert - 1));
				} else {
					output.println("You can buy nothing.");
					getControllerInterface().wakeUpServer(currentState.createTransation());
				}
			} catch (RemoteException | NumberFormatException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), e);
			}
		} else {
			waiting = true;
			pause();
		}

	}
	
	
	private void additionalOutput(SuperBonusState currentState) throws InvalidRegionException {
		if (currentState.isBuildingPemitTileBonus()) {
			swingUI.setConsoleText("\n\n" + currentState.useBonus());
			swingUI.enableRegionButtons(true);
			pause();
			String chosenRegion = swingUI.getChosenRegion();
			currentState.analyzeInput(chosenRegion);
		}
	}

	@Override
	public void visit(SuperBonusState currentState) {
		while (currentState.hasNext()) {		
			String selectedItem;
			int numberOfCurrentBonus = currentState.getCurrentBonusValue();
			try {
				for (int numberOfBonuses = 0; numberOfBonuses < numberOfCurrentBonus; numberOfBonuses++) {					
					additionalOutput(currentState);
					swingUI.setConsoleText("\n\n" + currentState.useBonus());
					if(currentState.isRecycleBuildingPermitBonus()) {
						//swingUI.enableTotalHandDeck(true);
						pause();
						selectedItem = String.valueOf(swingUI.getChosenTile());
					} 
					if(currentState.isRecycleRewardTokenBonus()) {
						swingUI.enableCities(true);
						pause();
					selectedItem = swingUI.getChosenCity();
					} else {
						//swingUI.enablePermissonTilePanel(swingUI.getChosenRegion());
						pause();
						selectedItem = String.valueOf(swingUI.getChosenTile());
					}
					swingUI.setConsoleText("\n\n" + currentState.useBonus());
					currentState.checkKey();
					currentState.addValue(selectedItem);
				}
			}catch (InvalidCityException | InvalidCardException | InvalidRegionException e) {
					Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), e);
					state.setExceptionString(e.toString());
				}
			}
		currentState.confirmChange();
		
		try {
			getControllerInterface().wakeUpServer(currentState.createSuperBonusesGiver());
		} catch (RemoteException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, CANNOT_REACH_SERVER_PRINT, e);
		}
		
	}

	@Override
	public void visit(EndGameState currentState) {
		swingUI.setConsoleText(currentState.getWinner());
		endGame = true;

	}
	
	private void sendAction(Action action) {
		try {
			getControllerInterface().wakeUpServer(action);
		} catch (RemoteException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, CANNOT_REACH_SERVER_PRINT, e);
		}
	}

	protected boolean waitResumeCondition() {
		return state instanceof StartTurnState || state instanceof MarketBuyPhaseState || state instanceof MarketOfferPhaseState;
	}

	@Override
	public void update(State state) {
		this.state = state;
		if(waitResumeCondition() && waiting) {
			resume();
			waiting = false;
		}
	}

	@Override
	public synchronized void run() {
		waiting = true;
		pause();
		waiting = false;
		do {
			state.acceptView(this);
		} while(!endGame);
	}
	
}
