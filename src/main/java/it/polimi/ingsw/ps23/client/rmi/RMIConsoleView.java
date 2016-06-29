package it.polimi.ingsw.ps23.client.rmi;

import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.polimi.ingsw.ps23.server.commons.exceptions.IllegalActionSelectedException;
import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidCardException;
import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidCostException;
import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidCouncilException;
import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidNumberOfAssistantException;
import it.polimi.ingsw.ps23.server.model.actions.Action;
import it.polimi.ingsw.ps23.server.model.bonus.Bonus;
import it.polimi.ingsw.ps23.server.model.player.Player;
import it.polimi.ingsw.ps23.server.model.state.AcquireBusinessPermitTileState;
import it.polimi.ingsw.ps23.server.model.state.AdditionalMainActionState;
import it.polimi.ingsw.ps23.server.model.state.AssistantToElectCouncillorState;
import it.polimi.ingsw.ps23.server.model.state.BuildEmporiumKingState;
import it.polimi.ingsw.ps23.server.model.state.BuildEmporiumPermitTileState;
import it.polimi.ingsw.ps23.server.model.state.ChangePermitsTileState;
import it.polimi.ingsw.ps23.server.model.state.ElectCouncillorState;
import it.polimi.ingsw.ps23.server.model.state.EndGameState;
import it.polimi.ingsw.ps23.server.model.state.EngageAnAssistantState;
import it.polimi.ingsw.ps23.server.model.state.MarketBuyPhaseState;
import it.polimi.ingsw.ps23.server.model.state.MarketOfferPhaseState;
import it.polimi.ingsw.ps23.server.model.state.StartTurnState;
import it.polimi.ingsw.ps23.server.model.state.State;
import it.polimi.ingsw.ps23.server.model.state.SuperBonusState;

class RMIConsoleView extends RMIView {

	private static final String CANNOT_REACH_SERVER_PRINT = "Cannot reach remote server.";

	private Scanner scanner;
	private PrintStream output;
	private State state;
	private boolean endGame;
	private boolean waiting;
	
	RMIConsoleView(String playerName, PrintStream output) {
		super(playerName);
		waiting = false;
		endGame = false;
		scanner = new Scanner(System.in);
		this.output = output;
	}

	@Override
	void setMapType(String mapType) {
		output.println("\nMap type: " + mapType + ".");
	}

	private void sendAction(Action action) {
		try {
			getControllerInterface().wakeUpServer(action);
		} catch (RemoteException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, CANNOT_REACH_SERVER_PRINT, e);
		}
	}

	@Override
	public void visit(StartTurnState currentState) {
		Player player = currentState.getCurrentPlayer();
		output.println(currentState.getStatus());
		if(player.getName().equals(getClientName())) {
			output.println("Current player: " + player.toString() + " " + player.showSecretStatus() + "\n" + currentState.getAvailableAction() + "\n\nChoose an action to perform? ");
			try {
				getControllerInterface().wakeUpServer(currentState.getStateCache().getAction(scanner.nextLine().toLowerCase()));
			} catch (NullPointerException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Cannot find the action.", e);
				try {
					getControllerInterface().wakeUpServer();
				} catch (RemoteException e1) {
					Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, CANNOT_REACH_SERVER_PRINT, e1);
				}
			} catch (RemoteException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, CANNOT_REACH_SERVER_PRINT, e);
			}
		} else {
			output.println("It's player " + player.getName() + " turn.");
			waiting = true;
			pause();
		}
	}

	@Override
	public void visit(ElectCouncillorState currentState) {
		output.println("Choose a free councillor from this list: " + currentState.getFreeCouncillors());
		String chosenCouncillor = scanner.nextLine().toLowerCase();
		output.println("Choose a balcony where to put the councillor: " + currentState.getCouncilsMap());
		String chosenBalcony = scanner.nextLine().toLowerCase();
		sendAction(currentState.createAction(chosenCouncillor, chosenBalcony));
	}

	@Override
	public void visit(AcquireBusinessPermitTileState currentState) {
		try {
			List<String> removedCards = new ArrayList<>();
			output.println("Choose a council to satisfy: " + currentState.getCouncilsMap());
			String chosenCouncil = scanner.nextLine().toLowerCase();
			output.println("How many cards to you want to use (max "
					+ currentState.getAvailablePoliticCardsNumber(chosenCouncil) + " )");
			int numberOfCards = Integer.parseInt(scanner.nextLine());
			for (int i = 0; i < numberOfCards && i < currentState.getPoliticHandSize(); i++) {
				output.println(
						"Choose a politic card you want to use from this list: " + currentState.getPoliticHandDeck());
				String chosenCard = scanner.nextLine().toLowerCase();
				removedCards.add(chosenCard);
			}
			output.println(
					"Choose a permission card (press 1 or 2): " + currentState.getAvailablePermitTile(chosenCouncil));
			int chosenCard = Integer.parseInt(scanner.nextLine()) - 1;
			sendAction(currentState.createAction(chosenCouncil, removedCards, chosenCard));
		} catch (InvalidCouncilException | InvalidCardException | NumberFormatException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), e);
			state.setExceptionString(e.toString());
		}
	}

	@Override
	public void visit(AssistantToElectCouncillorState currentState) {
		output.println("Choose a free councillor from this list: " + currentState.getFreeCouncillors());
		String chosenCouncillor = scanner.nextLine().toLowerCase();
		output.println("Choose a balcony where to put the councillor: " + currentState.getCouncilsMap());
		String chosenBalcony = scanner.nextLine().toLowerCase();
		sendAction(currentState.createAction(chosenCouncillor, chosenBalcony));
	}

	@Override
	public void visit(AdditionalMainActionState currentState) {
		sendAction(currentState.createAction());
	}

	@Override
	public void visit(EngageAnAssistantState currentState) {
		sendAction(currentState.createAction());
	}

	@Override
	public void visit(ChangePermitsTileState currentState) {
		output.println("Choose a region:" + currentState.printRegionalPermissionDecks());
		String chosenRegion = scanner.nextLine().toLowerCase();
		sendAction(currentState.createAction(chosenRegion));
	}

	@Override
	public void visit(BuildEmporiumKingState currentState) {
		List<String> removedCards = new ArrayList<>();
		try {
			output.println("Choose the number of cards you want for satisfy the King Council: "
					+ currentState.getAvailableCardsNumber());
		} catch (IllegalActionSelectedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), e);
			try {
				getControllerInterface().wakeUpServer(e);
			} catch (RemoteException e1) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, CANNOT_REACH_SERVER_PRINT, e1);
			}
		}
		try {
			int numberOfCards = Integer.parseInt(scanner.nextLine());
			output.println("Player hand deck:" + currentState.getDeck());
			for (int i = 0; i < numberOfCards && i < currentState.getPoliticHandSize(); i++) {
				output.println("Choose a politic card you want to use from this list: " + currentState.getAvailableCards());
				String chosenCard = scanner.nextLine().toLowerCase();
				removedCards.add(chosenCard);
			}
			output.println("please insert the route for the king.[king's initial position: "
					+ currentState.getKingPosition() + "] insert the arrival city: ");
			String arrivalCity = scanner.nextLine();
			sendAction(currentState.createAction(removedCards, arrivalCity));
		} catch (InvalidCardException | NumberFormatException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), e);
			state.setExceptionString(e.toString());
		}
	}

	@Override
	public void visit(BuildEmporiumPermitTileState currentState) {
		try {
			output.println("Choose the permit tile that you want to use for build an Emporium: (numerical input) "
					+ currentState.getAvaibleCards());
			int chosenCard = Integer.parseInt(scanner.nextLine()) - 1;
			output.println(
					"Choose the city where you what to build an emporium: " + currentState.getChosenCard(chosenCard));
			String chosenCity = scanner.nextLine();
			sendAction(currentState.createAction(chosenCity, chosenCard));
		} catch (IllegalActionSelectedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), e);
			try {
				getControllerInterface().wakeUpServer(e);
			} catch (RemoteException e1) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, CANNOT_REACH_SERVER_PRINT, e1);
			}
		} catch (InvalidCardException | NumberFormatException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), e);
			state.setExceptionString(e.toString());
		}

	}
	
	private List<String> sellPoliticCard(MarketOfferPhaseState currentState) throws NumberFormatException {
		List<String> chosenPoliticCards = new ArrayList<>();
		if (currentState.canSellPoliticCards()) {
			output.println("How many politic cards do you want to use? ");
			int numberOfCards = Integer.parseInt(scanner.nextLine());
			for (int i = 0; i < numberOfCards && i < currentState.getPoliticHandSize(); i++) {
				output.println("Select a card from this list: " + currentState.getPoliticHandDeck());
				chosenPoliticCards.add(scanner.nextLine());
			}
		}
		return chosenPoliticCards;
	}
	
	private List<Integer> sellPermissionCard(MarketOfferPhaseState currentState) throws NumberFormatException {
		List<Integer> chosenPermissionCards = new ArrayList<>();
		if (currentState.canSellPermissionCards()) {
			output.println("How many permission cards do you want to use? (numerical input >0)");
			int numberOfCards = Integer.parseInt(scanner.nextLine());
			for (int i = 0; i < numberOfCards && i < currentState.getPermissionHandSize(); i++) {
				output.println("Select a card from this list: " + currentState.getPermissionHandDeck());
				chosenPermissionCards.add(Integer.parseInt(scanner.nextLine()) - 1);
			}
		}
		return chosenPermissionCards;
	}
	
	private int sellAssistant(MarketOfferPhaseState currentState) throws NumberFormatException {
		int chosenAssistants = 0;
		if (currentState.canSellAssistants()) {
			output.println("Select the number of assistants " + currentState.getAssistants());
			chosenAssistants = Integer.parseInt(scanner.nextLine());
		}
		return chosenAssistants;
	}
	
	@Override
	public void visit(MarketOfferPhaseState currentState) {
		String player = currentState.getPlayerName();
		output.println("It's " + player + " market phase turn.");
		if (player.equals(getClientName())) {
			List<String> chosenPoliticCards = sellPoliticCard(currentState);
			List<Integer> chosenPermissionCards = sellPermissionCard(currentState);
			int chosenAssistants = sellAssistant(currentState);
			output.println("Choose the price for your offer: ");
			int cost = Integer.parseInt(scanner.nextLine());
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
		output.println("It's " + player + " market phase turn.");
		if(player.equals(getClientName())) {
			try {
				if (currentState.canBuy()) {
					output.println("Available offers: " + currentState.getAvaiableOffers());
					getControllerInterface()
							.wakeUpServer(currentState.createTransation(Integer.parseInt(scanner.nextLine())));
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

	@Override
	public void visit(SuperBonusState currentState) {// TODO gestione turni???
		Map<Bonus, List<String>> selectedBonuses = new HashMap<>();
		while (currentState.hasNext()) {
			Bonus currentBonus = currentState.getCurrentBonus();
			String chosenRegion = new String();
			int numberOfCurrentBonus = currentBonus.getValue();
			for (int numberOfBonuses = 0; numberOfBonuses < numberOfCurrentBonus; numberOfBonuses++) {
				if (currentState.isBuildingPemitTileBonus(currentBonus)) {
					output.println(currentState.useBonus(currentBonus));
					chosenRegion = scanner.nextLine().toLowerCase();
					currentState.analyzeInput(chosenRegion, currentBonus);
				}
				output.println(currentState.useBonus(currentBonus));
				List<String> bonusesSelections = new ArrayList<>();
				if (selectedBonuses.containsKey(currentBonus)) { // TODO verificare modifiche
					bonusesSelections = selectedBonuses.get(currentBonus);
				}
				if (currentState.isBuildingPemitTileBonus(currentBonus)) {
					bonusesSelections.add(chosenRegion);
					bonusesSelections.add(scanner.nextLine());
				} else {
					bonusesSelections.add(scanner.nextLine());
				}
				selectedBonuses.put(currentBonus, bonusesSelections);
			}
		}
		try {
			getControllerInterface().wakeUpServer(currentState.createSuperBonusesGiver(selectedBonuses));
		} catch (RemoteException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, CANNOT_REACH_SERVER_PRINT, e);
		}
	}

	@Override
	public void visit(EndGameState currentState) {
		output.println(currentState.getWinner());
		endGame = true;
	}

	private boolean waitResumeCondition() {
		return state instanceof StartTurnState || state instanceof MarketBuyPhaseState
				|| state instanceof MarketOfferPhaseState;
	}

	@Override
	public void update(State state) {
		this.state = state;
		if (waitResumeCondition() && waiting) {
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
			if (state.arePresentException()) {
				output.println(state.getExceptionString());
			}
		} while (!endGame);
	}

}