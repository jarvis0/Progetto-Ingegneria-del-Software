package it.polimi.ingsw.ps23.server.controller;

import it.polimi.ingsw.ps23.server.commons.viewcontroller.ControllerObserver;
import it.polimi.ingsw.ps23.server.model.Model;
import it.polimi.ingsw.ps23.server.model.actions.Action;
import it.polimi.ingsw.ps23.server.model.bonus.SuperBonusGiver;
import it.polimi.ingsw.ps23.server.model.market.MarketObject;
import it.polimi.ingsw.ps23.server.model.market.MarketTransation;
import it.polimi.ingsw.ps23.server.model.state.State;

public class Controller implements ControllerObserver {

	private final Model model;
	
	public Controller(Model model) {
		this.model = model;
	}

	@Override
	public void update() {
		model.setPlayerTurn();
	}

	@Override
	public void update(State state) {
		model.setActionState(state);
	}

	@Override
	public void update(Action action) {
		model.doAction(action);
		model.setPlayerTurn();
	}

	@Override
	public void update(MarketObject marketObject) {
		model.doOfferMarket(marketObject);
	}

	@Override
	public void update(MarketTransation marketTransation) {
		model.doBuyMarket(marketTransation);
	}

	@Override
	public void update(SuperBonusGiver superBonusGiver) {
		model.doSuperBonusesAcquisition(superBonusGiver);
	}

}