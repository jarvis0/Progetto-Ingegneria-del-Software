package it.polimi.ingsw.ps23.model.actions;

import javax.naming.InsufficientResourcesException;

import it.polimi.ingsw.ps23.model.Game;
import it.polimi.ingsw.ps23.model.TurnHandler;

public class AdditionalMainAction extends QuickAction {

	private static final int ASSISTANTS_COST = -3;
	
	public AdditionalMainAction() {
	}
	
	@Override
	public void doAction(Game game, TurnHandler turnHandler) {
		try {
			game.getCurrentPlayer().updateAssistants(ASSISTANTS_COST);
		} catch (InsufficientResourcesException e) {
			e.printStackTrace();
		}
		turnHandler.addMainAction();
		turnHandler.useQuickAction();
	}

}
