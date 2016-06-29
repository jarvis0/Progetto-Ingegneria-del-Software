package it.polimi.ingsw.ps23.client.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidCardException;
import it.polimi.ingsw.ps23.server.commons.exceptions.InvalidCouncilException;
import it.polimi.ingsw.ps23.server.model.actions.Action;
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

public class RMIGUIView extends RMIView {
	
	private static final String CANNOT_REACH_SERVER_PRINT = "Cannot reach remote server";
	
	private RMISwingUI rmiSwingUI;
	private State state;
	private boolean endGame;
	private boolean waiting;
	private boolean firstUIRefresh;
	
	RMIGUIView(String playerName) {
		super(playerName);
		firstUIRefresh = true;
	}

	public State getCurrentState() {
		return state;
	}

	@Override
	void setMapType(String mapType) {
		rmiSwingUI = new RMISwingUI(mapType, getClientName());
	}

	@Override
	public void visit(StartTurnState currentState) {
		if(firstUIRefresh) {
			rmiSwingUI.loadStaticContents(currentState);
			firstUIRefresh = false;
		}
		rmiSwingUI.refreshUI(currentState);
		Player player = currentState.getCurrentPlayer();
		if(player.getName().equals(getClientName())) {
			player.toString();
			rmiSwingUI.showAvailableActions(currentState.isAvailableMainAction(), currentState.isAvailableQuickAction(), this);
			pause();
			try {
				getControllerInterface().wakeUpServer(currentState.getStateCache().getAction(rmiSwingUI.getChosenAction()));
			} catch (RemoteException e) {
				e.printStackTrace();
			};
			
		} else {
			rmiSwingUI.showAvailableActions(false, false, this); //TODO creare metodo per stampare che è il turno di un altro player
			waiting = true;
			pause();
		}
		
	}

	@Override
	public void visit(ElectCouncillorState currentState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(EngageAnAssistantState currentState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ChangePermitsTileState currenState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AcquireBusinessPermitTileState currentState) {
		try {
			rmiSwingUI.enableButtons();
			List<String> removedCards = new ArrayList<>();
			pause();
			String chosenCouncil = rmiSwingUI.getChosenAction();
			int numberOfCards = 4;
			boolean finish = false;
			int i = 0;
			while (i < numberOfCards && i < currentState.getPoliticHandSize() && !finish) {
				pause();
				finish = rmiSwingUI.hasFinished();
				if(!finish) {
					removedCards.add(rmiSwingUI.getChosenCard());
				}
				i++;
			}
			pause();
			int chosenTile = rmiSwingUI.getChosenTile();
			sendAction(currentState.createAction(chosenCouncil, removedCards, chosenTile));
		} catch (InvalidCardException | NumberFormatException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), e);
			state.setExceptionString(e.toString());
		}

	}

	@Override
	public void visit(AssistantToElectCouncillorState currentState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AdditionalMainActionState currentState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BuildEmporiumKingState currentState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BuildEmporiumPermitTileState currentState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(MarketOfferPhaseState currentState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(MarketBuyPhaseState currentState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SuperBonusState currentState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(EndGameState currentState) {
		// TODO Auto-generated method stub

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
