package it.polimi.ingsw.ps23.server.model.state;

import java.io.Serializable;

import it.polimi.ingsw.ps23.server.commons.exceptions.IllegalActionSelectedException;
import it.polimi.ingsw.ps23.server.model.TurnHandler;

abstract class QuickActionState extends ActionState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1454397467441162084L;

	QuickActionState(String name) {
		super(name);
	}

	@Override
	public void canPerformThisAction(TurnHandler turnHandler) throws IllegalActionSelectedException {
		if (!turnHandler.isAvailableQuickAction()) {
			throw new IllegalActionSelectedException();
		}
	}
	
}
